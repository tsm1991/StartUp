package com.example.tasklib.startup.manager

import com.example.tasklib.startup.task.TsmStartTask
import java.util.*
import kotlin.collections.HashMap


//BFS 广度优先算法
class TsmStartUpTaskSort {

    // 通过子节点class 找到父节点
    var mChildTaskList:HashMap<Class<TsmStartTask<*>?>, TsmStartTask<*>> = HashMap();

    /// 记录这个节点的父节点的个数
    var mParentCount : HashMap<Class<*>,Int> =HashMap()

    //记录父节点下面的子节点个数
    var taskClassInfoMap : HashMap<Class<*>, TsmStartTask<*>?> = HashMap()

    //记录父节点下面的子节点个数
    var childTaskMap : HashMap<TsmStartTask<*>,MutableList<TsmStartTask<*>>> = HashMap()

    var mRootTaskQueue = LinkedList<TsmStartTask<*>?>()


    fun sortTasks(tasks:MutableList<out TsmStartTask<*>?>?):MutableList<TsmStartTask<*>?>?{
        if(tasks==null)
            return tasks
        /**
         * 先缓存所有类关系
         *
         */
        tasks?.forEach {
            taskClassInfoMap[it!!::class.java] = it
        }
        tasks?.forEach {
            it?.let {item->
                if(item?.getParentTaskCount()==0){
                    mRootTaskQueue.add(it)
                }else{
                    /**
                     * 如果有前置任务节点
                     * 先更新一下这个任务的前置节点的数量
                     * 遍历父节点,再通过class 找到对应前置任务,
                     * 更新前置节点的子任务
                     */
                    mParentCount[item::class.java] = item.getParentTaskCount()
                    item.getParentTasks()?.forEach {parent->
                        var p = taskClassInfoMap[parent]
                        if(p!=null){
                            var childList=childTaskMap[p]
                            if(childList==null){
                                childList= mutableListOf()
                            }
                            childList.add(item)
                            childTaskMap[p] = childList
                        }
                    }
                }
            }
        }
        var resultList :MutableList<TsmStartTask<*>?> = mutableListOf()

        var asyncList:MutableList<TsmStartTask<*>?> = mutableListOf()

        while (mRootTaskQueue.isNotEmpty()){
            var item = mRootTaskQueue.pop()
            if(item?.isRunOnMainThread()==true){
                resultList.add(item)
            }else{
                asyncList.add(item)
            }
            /**
             * 找到这个任务的所有子任务,
             * 并且获取这个任务当前的深度,如果为0,则添加到任务中
             * 不为0则等待其他任务执行
             */
            var childList=childTaskMap[item]
            if(childList!=null){
                childList?.forEach { chd->
                    var count=mParentCount[chd::class.java]
                    if(((count?:0)-1)==0){
                        mRootTaskQueue.offer(chd)
                    }else{
                        mParentCount[chd::class.java]=((count?:0)-1)
                    }
                }
            }
        }
        /**
         * 为了保证异步任务先调度
         * 这里将同步任务添加到同步任务之后
         */
        asyncList.addAll(resultList)
        return asyncList
    }
}
