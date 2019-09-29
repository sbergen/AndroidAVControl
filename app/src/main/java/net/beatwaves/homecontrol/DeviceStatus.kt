package net.beatwaves.homecontrol

import android.arch.lifecycle.MutableLiveData

class DeviceStatus {
    val busy =  MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()

    init {
        busy.value = false
        error.value = ""
    }
}