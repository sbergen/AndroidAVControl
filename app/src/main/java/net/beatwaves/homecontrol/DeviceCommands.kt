package net.beatwaves.homecontrol

interface DeviceCommands {
    fun powerOff(): Unit
    fun activateTvMode(): Unit
    fun activateUsbCMode(): Unit
    fun activateDexMode(): Unit
}