package com.example.tsmstartup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.tasklib.startup.manager.TsmStartUpManager
import com.example.tsmstartup.taskImpl.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /**
         * 这里拟定的任务执行顺序图
         *
         * 1-->2    -->3
         *     |          --> 6
         *     |-->4-->5
         *          |
         *          |--> 主线程从4执行完恢复
         *
         * 这里我给4号任务定义为主线程必须等待的任务,
         *
         * 多次重复点击,由于任务结果已经缓存,
         * 所以会导致第二次不回调用任务的初始化
         */
        findViewById<View>(R.id.tv_task_ok).setOnClickListener {
            TsmStartUpManager.Builder()
                .addTask(TsmTask3())
                .addTask(TsmTask2())
                .addTask(TsmTask1())
                .addTask(TsmTask5())
                .addTask(TsmTask6())
                .addTask(TsmTask4())
                .build()
                //   调用了  toWait 就会等待 标记了异步执行,并且需要主线程等待的任务
                .toWait()
            Log.i("tian.shm","----------主线程可以跑了-------------")
            Toast.makeText(this@MainActivity,"所有需要等待的任务都执行完成,其他任务可以继续异步执行",Toast.LENGTH_SHORT).show()
        }
    }
}