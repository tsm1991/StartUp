package com.example.tasklib.startup.lock

import java.util.concurrent.CountDownLatch


/**
 * 锁计数的持有者,为了方便任务  与  任务 manager 的调动,将他抽离出来
 */
abstract class LockParentImpl : StartUpLockParentInterface{

    /**
     * 异步初始化,并且让主线程等待的任务在插入时会将信号量 + 1
     * 该任务执行完成后会让信号量-1 ,为0时释放后续方法,
     */
    var countDownLatch : CountDownLatch

    constructor(){
        countDownLatch=CountDownLatch(getParentTaskCount())
    }



    constructor(count:Int){
        countDownLatch= CountDownLatch(count)
    }


    /**
     * 调用了这个方法主线程将会等待
     * 需要等待的异步线程的任务执行完毕
     */
    override fun toWait(){
        try {
            countDownLatch.await()
        }catch (e: Exception){

        }
    }


    override fun toNotify(){
        try {
            countDownLatch.countDown()
        }catch (e: Exception){

        }
    }

}