package test.mircod.com.test

import android.app.Application
import test.mircod.com.test.di.AppComponent
import test.mircod.com.test.di.AppModule
import test.mircod.com.test.di.DaggerAppComponent

class App : Application() {
    companion object {
//        lateinit var bleClient: RxBleClient
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
//        bleClient = RxBleClient.create(this)
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }
}