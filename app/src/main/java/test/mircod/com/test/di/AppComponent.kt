package test.mircod.com.test.di

import dagger.Component
import test.mircod.com.test.view.CharacteristicActivity
import test.mircod.com.test.view.MainActivity
import test.mircod.com.test.view.ServiceActivity
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun injectMain(act: MainActivity)
    fun injectServ(act: ServiceActivity)
    fun injectChar(act:CharacteristicActivity)
}