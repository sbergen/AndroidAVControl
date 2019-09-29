package net.beatwaves.homecontrol

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.*
import net.beatwaves.homecontrol.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity()
{
    lateinit var avm: AnthemDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.setLifecycleOwner(this)

        val vm = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding.vm = vm
        avm = vm.avm
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event == null) {
            return false;
        }

        val action = event.getAction()
        val ready = avm.status.busy.value != true
        when (event.getKeyCode()) {
            KEYCODE_VOLUME_UP -> {
                if (action == ACTION_DOWN && ready) {
                    avm.volumeUp()
                }
                return true
            }
            KEYCODE_VOLUME_DOWN -> {
                if (action == ACTION_DOWN && ready) {
                    avm.volumeDown()
                }
                return true
            }
            else -> return super.dispatchKeyEvent(event)
        }
    }
}
