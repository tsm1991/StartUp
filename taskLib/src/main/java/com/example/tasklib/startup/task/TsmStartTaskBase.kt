package com.example.tasklib.startup.task

import com.example.tasklib.startup.lock.StartUpLockParentInterface

interface TsmStartTaskBase<T> :Runnable , StartUpLockParentInterface {

    /**
     * 创建任务, 并返回结果,
     * 没有就返回Void ,
     * 有结果将结果保存起来
     */
    fun  createStartTask():T?

    /**
     * 运行线程位置
     */
    fun isRunOnMainThread():Boolean

    /**
     * 主线程必须等我,只有在异步的情况下,
     * 并且该参数为true  主线程就会等待该任务执行后再执行
     */
    fun mainThreadMustWaitForMe():Boolean

    /**
     * 获取前置任务的节点,设定任务时需要绘制任务执行时机图
     * bfs 这种排序会有多种结果,所以需要根据图来调整初始化顺序
     */
    fun getParentTasks():MutableList<Class<*>>?




}