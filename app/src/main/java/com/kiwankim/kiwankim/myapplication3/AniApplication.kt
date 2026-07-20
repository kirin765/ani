package com.kiwankim.kiwankim.myapplication3

import android.app.Application
import com.kiwankim.kiwankim.myapplication3.di.AppContainer
import com.kiwankim.kiwankim.myapplication3.notification.AiringNotifier

class AniApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        AiringNotifier.ensureChannel(this)
    }
}
