package com.example.tsmstartup.taskImpl

import android.os.SystemClock
import android.util.Log
import com.example.tasklib.startup.task.TsmStartTask
import kotlin.random.Random

class TsmTask2: TsmStartTask<Void>() {
    override fun createStartTask(): Void? {
        Log.i("tian.shm","---TsmTask2 start-------")
        SystemClock.sleep(500+ Random.nextLong(500))
        Log.i("tian.shm","---TsmTask2 end-------")
        return null
    }

    override fun isRunOnMainThread(): Boolean =true
    override fun getParentTasks(): MutableList<Class<*>>? {
        return mutableListOf(TsmTask1::class.java)
    }
}