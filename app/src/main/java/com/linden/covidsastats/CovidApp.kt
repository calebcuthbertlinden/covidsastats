package com.linden.covidsastats

import android.app.Application
import com.linden.covidsastats.mvi.dependency_injection.ToothpickActivityLifecycleCallbacks
import com.linden.covidsastats.mvi.model.CovidServiceApiModule
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieApplicationModule

class CovidApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Toothpick.inject(this, openApplicationScope(this))
        registerActivityLifecycleCallbacks(ToothpickActivityLifecycleCallbacks())
    }

    private fun openApplicationScope(app: Application): Scope {
        return Toothpick.openScope(app).apply {
            installModules(
                SmoothieApplicationModule(app),
                CovidServiceApiModule
            )
        }
    }

}