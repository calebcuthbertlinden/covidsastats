package com.linden.covidsastats.mvi.dependency_injection

import android.app.Activity
import android.app.Application
import android.os.Bundle
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule

class ToothpickActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity?, bundle: Bundle?) {
        activity?.apply {
            Toothpick.inject(this, openActivityScope(this))
        }

    }

    private fun openActivityScope(activity: Activity): Scope {
        return Toothpick.openScopes(activity.application, activity).apply {
            installModules(SmoothieActivityModule(activity))
        }
    }

    override fun onActivityDestroyed(activity: Activity?) {
        activity?.apply {
            Toothpick.closeScope(this)
        }
    }

    // Unused, moved to bottom of class for readability.
    override fun onActivityStarted(activity: Activity?) {}
    override fun onActivityPaused(activity: Activity?) {}
    override fun onActivityResumed(activity: Activity?) {}
    override fun onActivityStopped(activity: Activity?) {}
    override fun onActivitySaveInstanceState(activity: Activity?, bundle: Bundle?) {}

}