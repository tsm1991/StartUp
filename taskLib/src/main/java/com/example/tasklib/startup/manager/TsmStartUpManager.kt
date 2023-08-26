package com.example.tasklib.startup.manager

import android.os.Looper
import com.example.tasklib.startup.cache.TsmStartUpResultCacheManager
import com.example.tasklib.startup.lock.LockParentImpl
import com.example.tasklib.startup.task.TsmStartTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TsmStartUpManager : LockParentImpl {

    private var mTaskList= mutableListOf<TsmStartTask<*>>()

    /**
     * bfs算法,我也不知道我这种算不算
     */
    private var mTaskSort: TsmStartUpTaskSort = TsmStartUpTaskSort()

    /**
     * 如果希望这个线程池只有一个,可以将这个线程池放入到
     * TsmStartUpResultCacheManager中, 由于TsmStartUpResultCacheManager 是单利,
     * 所以就会只有一个了,这里会初始化多个
     */
    private var threadPoolExecutor : ExecutorService




    private constructor(mTaskList:MutableList<TsmStartTask<*>>, countDownLatch:Int):super(countDownLatch){
        this.mTaskList=mTaskList
        threadPoolExecutor=Executors.newFixedThreadPool(6)
    }


    fun sortList(){
        var list=mTaskSort.sortTasks(mTaskList)
        list?.forEach {
            it?.attachManager(this)
            /**
             * 主线程的任务就在主线程执行
             * 这里有一个问题,如果在主线程执行的任务先执行,由于是同步方法,导致后续异步任务添加时机延后
             * 这里的处理方式是在 TsmStartUpTaskSort 中 将 同步任务插入到异步任务之后
             * 这里同步任务就是最后执行的
             */
            if(it?.isRunOnMainThread()==true){
                it?.run()
            }else{
                //子线程的任务添加到线程池
                threadPoolExecutor.execute(it)
            }
        }
    }


    fun notifyTask(task: TsmStartTask<*>){
        // 找到子任务,然后执行子任务的信号量-1,控制子任务执行
        mTaskSort.childTaskMap[task]?.forEach {
            it.toNotify()
        }
        // 如果该任务需要主线程等待 ,那么就通知主线程
        if(!task.isRunOnMainThread()&&task.mainThreadMustWaitForMe()){
            try {
                countDownLatch.countDown()
            }catch (e:Exception){

            }
        }
    }


    override fun getParentTaskCount(): Int {
        return 0
    }


    /**
     * Builder  建造者模式
     * 使用这种模式可以让启动任务分开多个阶段
     *  比如  application onCreate 执行一部分 , 广告业执行一部分  ,  idle 执行一部分
     */
    class Builder{
        private var mTaskList= mutableListOf<TsmStartTask<*>>()
        private var countDownLatch=0

        fun addTask(task: TsmStartTask<*>): Builder {
            /**
             * 已经有结果的任务不用再执行了
             *
             * 这里有一个问题,那就是如果同样是异步任务,在同一批次执行,那么就会导致这个任务会执行多次
             * 想要制止这种问题,就需要在  TsmStartUpResultCacheManager 中缓存正在执行的任务,在任务执行完成后移除
             * 这种方式是借鉴 okhttp 请求的方式,但是我这里没有写
             */
            if(TsmStartUpResultCacheManager.instance.getCacheResult(task::class.java)==null){
                mTaskList.add(task)
                if(!task.isRunOnMainThread()&&task.mainThreadMustWaitForMe()){
                    countDownLatch++
                }
            }
            return this
        }




        fun addAllTask(tasks: MutableList<TsmStartTask<*>>): Builder {
            tasks?.forEach {
                // 校验重复的任务
                if(TsmStartUpResultCacheManager.instance.getCacheResult(it::class.java)==null){
                    mTaskList.add(it)
                }
            }
            return this
        }

        fun build(): TsmStartUpManager {
            if(Thread.currentThread()!=Looper.getMainLooper().thread){
                throw IllegalThreadStateException("you must build TsmStartUpManager on Main Thread")
            }
            var manager= TsmStartUpManager(mTaskList,countDownLatch)
            manager.sortList()
            return manager
        }
    }
}