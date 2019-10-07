package net.beatwaves.homecontrol

import kotlinx.coroutines.*

class SmartThingsDevice : Device() {

    private val api: SmartThingsApi = SmartThingsApiFactory.smartThingsApi

    val powerOnCommand = Command("switch", "on")
    val powerOffCommand = Command("switch", "off")
    val switchToAvmInputCommand = inputCommand("HDMI2")
    val switchToUsbCInputCommand = inputCommand("HDMI1")

    override fun powerOff() {
        scope.runAction {
            sendCommand(arrayOf(powerOffCommand), "power off").await()
        }
    }

    override fun activateTvMode() {
        scope.runAction {
            sendCommand(arrayOf(powerOnCommand), "power on").await()
        }
    }

    override fun activateUsbCMode() {
        scope.runAction {
            val commands = arrayOf(powerOnCommand, switchToUsbCInputCommand)
            sendCommand(commands, "power on + switch to USB-C").await()
        }
    }

    override fun activateDexMode() {
        scope.runAction {
            val commands = arrayOf(powerOnCommand, switchToAvmInputCommand)
            sendCommand(commands, "power on + switch to AVM").await()
        }
    }

    private fun inputCommand(input: String) = Command("mediaInputSource", "setInputSource", arrayOf(input))

    private suspend fun sendCommand(commands: Array<Command>, description: String): Deferred<Unit> = scope.async {
        val response = api.executeCommand(Commands(commands)).await()
        if (!response.isSuccessful || response.body()?.isSuccess == false)
        {
            throw Exception("Failed to $description: ${response.body()?.error ?: response.message()}")
        }
    }
}