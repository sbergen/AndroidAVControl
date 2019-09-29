package net.beatwaves.homecontrol

import kotlinx.coroutines.*
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

class DeviceCoroutineScope constructor(private val status: DeviceStatus) : Closeable {
    private lateinit var parentJob: Job
    private lateinit var scope: CoroutineScope

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    init {
        buildCrContext()
    }

    private fun buildCrContext() {
        parentJob = Job()
        scope = CoroutineScope(coroutineContext)
    }

    override fun close() {
        coroutineContext.cancel()
    }

    val crErrorHandler = CoroutineExceptionHandler { _, exception ->
        status.busy.postValue(false)
        status.error.postValue(exception.message)
        buildCrContext()
    }

    fun runAction(block: suspend CoroutineScope.() -> Unit) {
        status.busy.postValue(true)
        status.error.postValue("")

        scope.launch(crErrorHandler) {
            block()
            status.busy.postValue(false)
        }
    }

    fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T> =
        scope.async(crErrorHandler, CoroutineStart.DEFAULT, block)
}