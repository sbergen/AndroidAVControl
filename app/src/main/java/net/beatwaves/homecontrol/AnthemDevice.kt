package net.beatwaves.homecontrol

import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.lang.StringBuilder
import java.net.InetSocketAddress
import java.net.Socket

class AnthemDevice : Device() {
    private val timeout = 3000;
    private val defaultBrightness = "2" // Medium

    val screenOn = MutableLiveData<Boolean>()

    init {
        runActions { socket ->
            var screenIsOn: Boolean? = null
            try {
                val brightness = query(socket, panelBrightness("?"), brightnessRegex)
                    .getOrNull(1)
                    ?.toIntOrNull();

                screenIsOn = brightness != null && brightness != 0
                screenOn.postValue(screenIsOn)
            }
            finally {
                screenOn.observeForever { shouldBeOn -> runActions { socket ->
                    // Avoid setting this when initializing, re-set on failure
                    if (screenIsOn != shouldBeOn) {
                        val command = panelBrightness(if (shouldBeOn == true) defaultBrightness else "0")
                        assertCommand(socket, command, brightnessRegex)
                        screenIsOn = shouldBeOn
                    }
                }}
            }
        }
    }

    override fun powerOff() {
        runActions { socket ->
            assertCommand(socket, power(false))
        }
    }

    override fun activateTvMode() {
        runActions { socket ->
            powerOn(socket)
            assertCommand(socket, input(2))
        }
    }

    override fun activateUsbCMode() {
        runActions { socket ->
            powerOn(socket)
            assertCommand(socket, input(3))
        }
    }

    override fun activateDexMode() {
        runActions { socket ->
            powerOn(socket)
            assertCommand(socket, input(2))
        }
    }

    fun volumeUp() {
        runActions { socket ->
            assertCommand(socket, volumeUp(1), volumeResponseRegex)
        }
    }

    fun volumeDown() {
        runActions { socket ->
            assertCommand(socket, volumeDown(1), volumeResponseRegex)
        }
    }

    private fun powerOn(socket: Socket) {
        repeat(2) {
            assertCommand(socket, power(true))
        }
    }

    private fun power(on: Boolean) = "Z1POW${if (on) 1 else 0};"
    private fun input(i: Int) = "Z1INP$i;"

    private val volumeResponseRegex = Regex("""Z1VOL-?\d+;""")
    private fun volumeUp(increment: Int) = "Z1VUP$increment;"
    private fun volumeDown(decrement: Int) = "Z1VDN$decrement;"

    private val brightnessRegex = Regex("""FPB(\d);""")
    private fun panelBrightness(value: String) = "FPB$value;"


    private fun runActions(actions: (Socket) -> Unit) {
        scope.runAction { openSocket().use { actions(it) } }
    }

    private fun query(socket: Socket, command: String, expectedResponse: Regex): List<String>
    {
        val result = executeCommand(socket, command)
        val match = expectedResponse.matchEntire(result)

        if (match == null) {
            throw Exception("Could not parse $result")
        }
        else {
            return match.groupValues
        }
    }

    private fun assertCommand(socket: Socket, command: String, expectedResponse: Regex? = null) {
        val ackResponse = ";"
        val result = executeCommand(socket, command)

        // Some commands just ack directly
        if (result == ackResponse)
        {
            return;
        }

        // Some commands return something else
        if (expectedResponse != null) {
            if (!expectedResponse.matches(result)) {
                throw Exception("Got unexpected response: $result")
            }
        }
        // Some commands echo their result
        else if (result != command) {
            throw Exception("Got $result instead of echoed command")
        }

        // And all commands should send an ack at the end
        val waitResult = readUntilSemicolon(socket)
        if (waitResult != ackResponse) {
            throw Exception("Got $waitResult instead of ack")
        }
    }

    private fun openSocket(): Socket {
        val socket = Socket()
        socket.connect(InetSocketAddress(BuildConfig.AVM_IP, 14999), timeout)
        return socket;
    }


    private fun executeCommand(socket: Socket, command: String): String {
        socket.soTimeout = timeout
        return runBlocking(Dispatchers.IO) {
            val writer = socket.getOutputStream().writer()
            writer.write(command)
            writer.flush()

            readUntilSemicolon(socket)
        }
    }

    private fun readUntilSemicolon(socket: Socket): String {
        val sb = StringBuilder()

        val stream = socket.getInputStream()
        while (true) {
            val char = stream.read().toChar()
            sb.append(char)

            if (char == ';') {
                break
            }
        }

        return sb.toString()
    }

}