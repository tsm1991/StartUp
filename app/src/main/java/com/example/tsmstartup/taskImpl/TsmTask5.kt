package com.example.tsmstartup.taskImpl

import android.os.SystemClock
import android.util.Log
import com.example.tasklib.startup.task.TsmStartTask
import kotlin.random.Random

class TsmTask5 : TsmStartTask<String>() {

    override fun createStartTask(): String? {
        Log.i("tian.shm","---TsmTask5 start-------")
        SystemClock.sleep(500+ Random.nextLong(500))
        Log.i("tian.shm","---TsmTask5 end-------")
        return "TsmTask5"
    }

    override fun isRunOnMainThread(): Boolean =false
    override fun getParentTasks(): MutableList<Class<*>>? {
        return mutableListOf(TsmTask4::class.java)
    }
}