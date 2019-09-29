package net.beatwaves.homecontrol

import java.io.Closeable

abstract class Device : DeviceCommands, Closeable {

    val status = DeviceStatus()
    protected val scope = DeviceCoroutineScope(status)

    override fun close() {
        scope.close()
    }
}