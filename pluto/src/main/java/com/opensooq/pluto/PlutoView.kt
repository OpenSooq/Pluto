package com.opensooq.pluto

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SnapHelper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.listeners.OnSlideChangeListener
import com.opensooq.pluto.listeners.OnSnapPositionChangeListener
import com.opensooq.pluto.listeners.SnapOnScrollListener
import java.lang.ref.WeakReference
import java.util.*
import kotlin.Experimental.Level

/**
 * Created by Omar Altamimi on 28,April,2019
 */

class PlutoView @JvmOverloads constructor(context: Context,
                                          attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.PlutoViewStyle) : FrameLayout(context, attrs, defStyleAttr), ViewActionHandler {


    private var rvSlider: RecyclerView?
    private var onSlideChangeListener: OnSlideChangeListener? = null
    private var duration: Long = DEFAULT_DURATION
    private var currentPosition: Int = 0
    private var indicator: PlutoIndicator? = null
    private var cycleTimer: Timer? = null
    private var cycleTask: TimerTask? = null
    private var plutoAdapter: PlutoAdapter<*, *>? = null

    private var resumingTimer: Timer? = null
    var isCycling: Boolean = false
    private var autoRecover = true
    private var autoCycle: Boolean = false
    private lateinit var helper: SnapHelper
    private var onScrollListener: RecyclerView.OnScrollListener? = null
    private var resumingTask: TimerTask? = null
    private var wasCycling: Boolean = false
    private var plutoLifeCycleObserver: PlutoLifeCycleObserver

    /**
     * Set the visibility of the indicators.
     */
    var indicatorVisibility: Boolean = false
        set(visibility) {
            field =visibility
            indicator?.setVisibility(visibility)
        }

    private val mh = IncomingHandler(this)
    private fun setupAdapter() {
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        linearLayoutManager.initialPrefetchItemCount = 4
        rvSlider?.apply {
            layoutManager = linearLayoutManager
            adapter = plutoAdapter
        }
        addScrollListener()
    }

    init {
        View.inflate(getContext(), R.layout.layout_view_slider, this)
        val attributes = context.theme.obtainStyledAttributes(attrs,
                R.styleable.PlutoView,
                defStyleAttr, 0)

        rvSlider = findViewById(R.id.rvSlider)
        autoCycle = attributes.getBoolean(R.styleable.PlutoView_auto_cycle, true)
        indicatorVisibility = attributes.getBoolean(R.styleable.PlutoView_indicator_visibility,
                false)
        rvSlider?.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {

            override fun onInterceptTouchEvent(recyclerView: RecyclerView,
                                               motionEvent: MotionEvent): Boolean {
                val action = motionEvent.action
                if (action == MotionEvent.ACTION_UP) {
                    recoverCycle()
                }
                return false
            }

            override fun onTouchEvent(recyclerView: RecyclerView,
                                      motionEvent: MotionEvent) {

            }

            override fun onRequestDisallowInterceptTouchEvent(b: Boolean) {

            }
        })

        attributes.recycle()
        if (autoCycle) {
            startAutoCycle()
        }
        plutoLifeCycleObserver = PlutoLifeCycleObserver()
    }

    private fun initScrollListener() {
        onScrollListener = null
        onScrollListener = SnapOnScrollListener(helper, object : OnSnapPositionChangeListener {
            override fun onSnapPositionChange(position: Int) {
                currentPosition = position
                plutoAdapter?.let {

                    onSlideChangeListener?.onSlideChange(it,
                            position % it.realCount)
                }

            }
        })
    }

    @JvmOverloads
    fun create(adapter: PlutoAdapter<*, *>, duration: Long = DEFAULT_DURATION, lifecycle: Lifecycle) {
        this.plutoAdapter = adapter
        plutoLifeCycleObserver.registerActionHandler(this)
        plutoLifeCycleObserver.registerLifecycle(lifecycle)
        setupAdapter()
        this.duration = duration
        setIndicatorPosition(IndicatorPosition.CENTER_BOTTOM)
    }


    enum class IndicatorPosition(val position: String, val resourceId: Int) {
        CENTER_BOTTOM("CENTER_BOTTOM", R.id.default_center_bottom_indicator),
        RIGHT_BOTTOM("RIGHT_BOTTOM", R.id.default_bottom_end_indicator),
        LEFT_BOTTOM("LEFT_BOTTOM", R.id.default_bottom_start_indicator),
        CENTER_TOP("CENTER_TOP", R.id.default_center_top_indicator),
        RIGHT_TOP("RIGHT_TOP", R.id.default_center_top_end_indicator),
        LEFT_TOP("LEFT_TOP", R.id.default_center_top_start_indicator);

        override fun toString(): String {
            return name
        }
    }

    fun setIndicatorPosition(presetIndicator: IndicatorPosition) {
        val indicator = findViewById<PlutoIndicator>(presetIndicator.resourceId)
        setCustomIndicator(indicator)
    }

    fun setCustomIndicator(indicator: PlutoIndicator) {
        this.indicator?.destroySelf()
        this.indicator = indicator
        this.indicator?.apply {
            setVisibility(this@PlutoView.indicatorVisibility)
            setRecyclerView(rvSlider, helper)
            redraw()
        }
    }


    private fun addScrollListener() {
        helper = PagerSnapHelper()
        rvSlider?.onFlingListener = null
        helper.attachToRecyclerView(rvSlider)

        onScrollListener?.let { rvSlider?.removeOnScrollListener(it) }
        initScrollListener()
        onScrollListener?.let {
            rvSlider?.addOnScrollListener(it)

        }
    }

    fun setOnSlideChangeListener(onSlideChangeListener: OnSlideChangeListener) {
        this.onSlideChangeListener = onSlideChangeListener

    }


    fun getDuration(): Long {
        return duration
    }


    /**
     * get the current item position
     *
     * @return
     */
    fun getCurrentPosition(): Int {
        if (plutoAdapter == null)
            throw IllegalStateException("You did not set a slider adapter")
        return currentPosition % plutoAdapter?.realCount!!
    }

    /**
     * set current slider
     *
     * @param position
     */
    @JvmOverloads
    fun setCurrentPosition(position: Int, smooth: Boolean = true) {
        plutoAdapter?.let {
            if (position >= it.realCount || position < 0) {
                throw IndexOutOfBoundsException("trying to access position" + position + " where size" + it.realCount)
            }
            currentPosition = it.realCount * PlutoAdapter.MULTIPLY + position
            if (smooth) {
                rvSlider?.smoothScrollToPosition(currentPosition)
            } else {
                rvSlider?.scrollToPosition(currentPosition)

            }

        } ?: destroyPluto()

    }

    /**
     * move to prev slide.
     */
    @JvmOverloads
    fun movePrevPosition(smooth: Boolean = true) {
        currentPosition--

        if (plutoAdapter == null)
            destroyPluto()
        if (smooth) {
            rvSlider?.smoothScrollToPosition(currentPosition)
        } else {
            rvSlider?.scrollToPosition(currentPosition)
        }

    }

    /**
     * move to next slide.
     */
    @JvmOverloads
    fun moveNextPosition(smooth: Boolean = true) {
        if (currentPosition + 1 > plutoAdapter?.getItemCount() ?: 0)
            return
        currentPosition++

        if (plutoAdapter == null)
            destroyPluto()
        if (smooth) {
            rvSlider?.smoothScrollToPosition(currentPosition)
        } else {
            rvSlider?.scrollToPosition(currentPosition)
        }
    }

    /**
     * start auto cycle.
     *
     * @param delay       delay time
     * @param duration    animation duration time.
     * @param autoRecover if recover after user touches the slider.
     */
    @JvmOverloads
    fun startAutoCycle(duration: Long = this.duration,
                       autoRecover: Boolean = this.autoRecover) {

        cycleTask?.cancel()
        mh.removeCallbacksAndMessages(null)
        cycleTimer?.cancel()
        resumingTimer?.cancel()
        resumingTask?.cancel()
        this.duration = duration
        cycleTimer = Timer()
        this.autoRecover = autoRecover
        cycleTask = CycleTask(mh)
        cycleTimer?.schedule(cycleTask, DELAY_TIME, duration)
        isCycling = true
        autoCycle = true
    }

    /**
     * runtime call
     * pause auto cycle.
     */
    private fun pauseAutoCycle() {

        if (isCycling) {
            cycleTimer?.cancel()
            cycleTask?.cancel()
            mh.removeCallbacksAndMessages(null)

            isCycling = false
            wasCycling = true
        } else {
            if (resumingTimer != null && resumingTask != null) {
                recoverCycle()
            }
        }

    }


    /**
     * when paused cycle, this method can wake it up.
     */
    private fun recoverCycle() {
        if (!autoRecover || !autoCycle) {
            return
        }

        if (!isCycling) {
            if (resumingTask != null && resumingTimer != null) {
                resumingTimer?.cancel()
                resumingTask?.cancel()
            }
            resumingTimer = Timer()
            resumingTask = object : TimerTask() {
                override fun run() {
                    startAutoCycle()
                }
            }
            try {
                resumingTimer?.schedule(resumingTask, DELAY_TIME)
            } catch (ignore: IllegalStateException) {

            }
        }
    }

    /**
     * set the duration between two slider changes. the duration value must bigger or equal to 500
     *
     * @param duration how long
     */
    fun setDuration(duration: Long) {
        if (duration >= 500) {
            this.duration = duration
            if (autoCycle && isCycling) {
                startAutoCycle()
            }
        }
    }

    /**
     * stop the auto circle
     */
    fun stopAutoCycle() {
        cycleTask?.cancel()
        mh.removeCallbacksAndMessages(null)

        cycleTimer?.cancel()
        resumingTimer?.cancel()
        resumingTask?.cancel()
        autoCycle = false
        isCycling = false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        if (action == MotionEvent.ACTION_DOWN) {
            pauseAutoCycle()
        }
        return false
    }

    private fun destroyPluto() {
        cycleTask?.cancel()
        mh.removeCallbacksAndMessages(null)

        cycleTimer?.cancel()
        resumingTimer?.cancel()
        resumingTask?.cancel()
        mh.removeCallbacksAndMessages(null)
        rvSlider?.adapter = null
        indicator?.destroySelf()
        onScrollListener?.let { rvSlider?.removeOnScrollListener(it) }
        onScrollListener = null
        onSlideChangeListener = null
        cycleTimer = null
        cycleTask = null
        resumingTask = null
        resumingTimer = null
        plutoLifeCycleObserver.unregister()
    }

    internal class IncomingHandler(plutoView: PlutoView) : Handler() {
        private val mSliderWeakReference: WeakReference<PlutoView> = WeakReference(plutoView)

        override fun handleMessage(msg: Message) {
            val plutoView = mSliderWeakReference.get()
            plutoView?.moveNextPosition()
        }
    }

    @DontUse
    override fun onResumed() {
        if (wasCycling) {
            recoverCycle()
        }

    }

    @DontUse
    override fun onPause() {
        pauseAutoCycle()
    }

    @DontUse
    override fun onDestroy() {
        destroyPluto()
    }


    companion object {
        private const val DELAY_TIME = 3000L
        private const val DEFAULT_DURATION = 4000L
    }

    @Experimental(level = Level.ERROR)
    private annotation class DontUse

    private class CycleTask(private val incomingHandler: IncomingHandler?) : TimerTask() {

        override fun run() {
            incomingHandler?.sendEmptyMessage(0)
        }

    }


}
