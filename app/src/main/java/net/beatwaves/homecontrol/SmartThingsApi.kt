package net.beatwaves.homecontrol

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class ValueWrapper(
    val value: String
)

data class SwitchData(
    val switch: ValueWrapper
)

data class InputSourceData(
    val inputSource: ValueWrapper
)

data class MainData(
    val switch: SwitchData,
    val mediaInputSource: InputSourceData
)

data class Components(
    val main: MainData
)

data class TvStatus(
    val components: Components) {

    val isPoweredOn get() = components.main.switch.switch.value == "on"
    val inputSource get() = components.main.mediaInputSource.inputSource.value
}

data class Command(
    val capability: String,
    val command: String,
    val arguments: Array<out Any>? = null
)

data class Commands(
    val commands: Array<Command>
)

data class CommandsResponse(
    val error: String?) {

    val isSuccess get() = error.isNullOrEmpty();
}

interface SmartThingsApi {

    @GET("status")
    fun getStatus(): Deferred<Response<TvStatus>>

    @POST("commands")
    fun executeCommand(@Body commands: Commands): Deferred<Response<CommandsResponse>>
}