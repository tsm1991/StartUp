package com.example.tasklib.startup.lock

interface StartUpLockParentInterface {

    /**
     * 等待
     */
    fun toWait()

    /**
     * 唤醒
     */
    fun toNotify()

    /**
     * 获取依赖了前面几个
     */
    fun getParentTaskCount():Int
}