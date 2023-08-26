package com.example.tsmstartup.taskImpl

import android.os.SystemClock
import android.util.Log
import com.example.tasklib.startup.task.TsmStartTask
import kotlin.random.Random

class TsmTask4 : TsmStartTask<Int>() {
    override fun createStartTask(): Int {
        Log.i("tian.shm","---TsmTask4 start-------")
        SystemClock.sleep(500+ Random.nextLong(500))
        Log.i("tian.shm","---TsmTask4 end-------")
        return 111
    }

    override fun isRunOnMainThread(): Boolean =false
    override fun getParentTasks(): MutableList<Class<*>>? {
        return mutableListOf(TsmTask2::class.java)
    }

    override fun mainThreadMustWaitForMe(): Boolean {
        return true
    }
}