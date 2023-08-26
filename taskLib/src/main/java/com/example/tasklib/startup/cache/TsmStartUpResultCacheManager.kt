package com.example.tasklib.startup.cache

import java.util.concurrent.ConcurrentHashMap



/**
 * 如果后续节点需要前置节点的结果,这里使用这个缓存
 */
class TsmStartUpResultCacheManager {

    companion object{
        // by lazy 线程安全 JvmStatic 静态,让他变成gc root
        @JvmStatic
        val instance: TsmStartUpResultCacheManager by lazy {
            TsmStartUpResultCacheManager()
        }
    }

    var mResult :ConcurrentHashMap<Class<*>, TsmCacheResult<*>> = ConcurrentHashMap()


    fun cacheResult(cls:Class<*>,result: TsmCacheResult<*>){
        mResult[cls] = result
    }
    fun getCacheResult(cls:Class<*>): TsmCacheResult<*>?{
        return mResult[cls]
    }
}