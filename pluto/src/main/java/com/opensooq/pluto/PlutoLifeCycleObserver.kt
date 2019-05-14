package com.opensooq.pluto

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent

/**
 * Created by omaraltamimi on 14,May,2019
 */
internal class PlutoLifeCycleObserver : LifecycleObserver {
    private var actionHandler: ViewActionHandler? = null
    private lateinit var lifecycle: Lifecycle
    internal fun registerActionHandler(handler: ViewActionHandler) {
        this.actionHandler = handler
    }

    internal fun registerLifecycle(lifecycle: Lifecycle) {
        this.lifecycle = lifecycle
        this.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    internal    fun onResume() {
        this.actionHandler?.onResumed()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    internal  fun stop() {
        this.actionHandler?.onDestroy()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    internal  fun onDestroy() {
        this.actionHandler?.onPause()
    }

    internal fun unregister() {
        this.actionHandler = null
        lifecycle.removeObserver(this)
    }
}

internal interface ViewActionHandler {
    fun onResumed()
    fun onPause()
    fun onDestroy()
}