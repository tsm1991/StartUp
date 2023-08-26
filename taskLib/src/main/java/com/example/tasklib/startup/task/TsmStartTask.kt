package com.example.tasklib.startup.task

import com.example.tasklib.startup.cache.TsmCacheResult
import com.example.tasklib.startup.cache.TsmStartUpResultCacheManager
import com.example.tasklib.startup.lock.LockParentImpl
import com.example.tasklib.startup.manager.TsmStartUpManager

/**
 * 基础封装 ,定义基础属性,这里定义了信号量,
 * 前置任务执行完毕后,通过 bfs 缓存找到对应的缓存关系,
 * 即可调用的task 的 toNofity
 */
abstract class TsmStartTask<T> : LockParentImpl(),  TsmStartTaskBase<T> {




    var manager: TsmStartUpManager?=null

    fun attachManager(manager: TsmStartUpManager){
        this.manager=manager
    }


    override fun run() {
        if(manager!=null){
            toWait()
        }
        var result=createStartTask()
        TsmStartUpResultCacheManager.instance.cacheResult(this::class.java, TsmCacheResult(result))
        if(manager!=null){
            manager?.notifyTask(this)
        }
    }

    override fun mainThreadMustWaitForMe() =false


    override fun getParentTaskCount(): Int {
        return getParentTasks()?.size?:0
    }




}