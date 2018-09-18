package test.mircod.com.test

import android.app.Application
import com.polidea.rxandroidble2.RxBleClient

class App : Application() {
    companion object {
        lateinit var bleClient: RxBleClient
    }

    override fun onCreate() {
        super.onCreate()
        bleClient = RxBleClient.create(this)
    }
}