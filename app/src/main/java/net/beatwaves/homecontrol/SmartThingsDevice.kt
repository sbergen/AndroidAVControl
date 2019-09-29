package net.beatwaves.homecontrol

import kotlinx.coroutines.*

class SmartThingsDevice : Device() {

    private val api: SmartThingsApi = SmartThingsApiFactory.smartThingsApi

    val powerOnCommand = Command("switch", "on")
    val powerOffCommand = Command("switch", "off")
    val switchToAnthemInputCommand = inputCommand("HDMI2")
    val switchToUsbCInputCommand = inputCommand("HDMI1")

    override fun powerOff() {
        scope.runAction {
            sendCommand(powerOffCommand, "power off").await()
        }
    }

    override fun activateTvMode() {
        scope.runAction {
            powerOn().await()
        }
    }

    override fun activateUsbCMode() {
        scope.runAction {
            powerOn().await()
            sendCommand(switchToUsbCInputCommand, "switch input").await()
        }
    }

    override fun activateDexMode() {
        scope.runAction {
            powerOn().await()
            sendCommand(switchToAnthemInputCommand, "switch input").await()
        }
    }

    private fun inputCommand(input: String) = Command("mediaInputSource", "setInputSource", arrayOf(input))

    private suspend fun getPowerStatus() = scope.async {
        val statusResponse = api.getStatus().await()
        if (!statusResponse.isSuccessful)
        {
            throw Exception("Failed to get status: ${statusResponse.message()}")
        }

    statusResponse.body()!!.isPoweredOn
    }

    private suspend fun powerOn() = scope.async {
        val repeatTimes = 3;
        var success = getPowerStatus().await()
        repeat(3) {
            if (!success)
            {
                sendCommand(powerOnCommand, "power on").await()
                delay(5000)
                success = getPowerStatus().await()
            }
        }

        if (!success)
        {
            throw Exception("Failed to power on after $repeatTimes attempts!")
        }
    }

    private suspend fun sendCommand(command: Command, description: String): Deferred<Unit> = scope.async {
        val response = api.executeCommand(Commands(arrayOf(command))).await()
        if (!response.isSuccessful || response.body()?.isSuccess == false)
        {
            throw Exception("Failed to $description: ${response.body()?.error ?: response.message()}")
        }
    }
}