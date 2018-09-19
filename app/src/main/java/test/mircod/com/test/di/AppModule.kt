package test.mircod.com.test.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import test.mircod.com.test.model.BluetoothModule
import javax.inject.Singleton

@Module
class AppModule(val app: Application) {
    @Provides
    @Singleton
    fun provideContext(): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideBleModule(ctx: Context): BluetoothModule = BluetoothModule(ctx)
}