package net.beatwaves.homecontrol

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class MainViewModel : ViewModel(), DeviceCommands {
    val tv = SmartThingsDevice()
    val avm = AnthemDevice()

    val busy = MutableLiveData<Boolean>()

    init {
        tv.status.busy
            .combineLatest(avm.status.busy)
            .observeForever { statuses ->
                busy.value = statuses?.first == true || statuses?.second == true }
    }

    override fun onCleared() {
        tv.close()
        avm.close()
    }

    override fun powerOff() {
        doWithDevices { it.powerOff() }
    }

    override fun activateTvMode() {
        doWithDevices { it.activateTvMode() }
    }

    override fun activateUsbCMode() {
        doWithDevices { it.activateUsbCMode() }
    }

    override fun activateDexMode() {
        doWithDevices { it.activateDexMode() }
    }

    private fun doWithDevices(action: (DeviceCommands) -> Unit) {
        action(tv)
        action(avm)
    }
}