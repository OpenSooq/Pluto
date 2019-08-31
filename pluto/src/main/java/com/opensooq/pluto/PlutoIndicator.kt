package com.opensooq.pluto

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.support.annotation.DrawableRes
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SnapHelper
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.listeners.OnSnapPositionChangeListener
import com.opensooq.pluto.listeners.SnapOnScrollListener
import java.util.*

/**
 * Created by Omar Altamimi on 28,April,2019
 */

class PlutoIndicator @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null) : LinearLayout(mContext, attrs) {

    private var mRecyclerView: RecyclerView? = null

    /**
     * Variable to remember the previous selected indicator.
     */
    private var mPreviousSelectedIndicator: ImageView? = null

    /**
     * Previous selected indicator position.
     */
    private var mPreviousSelectedPosition: Int = 0

    /**
     * Custom selected indicator style resource id.
     */
    var unSelectedIndicatorResId: Int = 0
        private set


    /**
     * Custom unselected indicator style resource id.
     */
    var selectedIndicatorResId: Int = 0
        private set

    private var mSelectedDrawable: Drawable? = null
    private var mUnselectedDrawable: Drawable? = null
    internal var snapOnScrollListener: SnapOnScrollListener? = null
    private var mItemCount = 0

    val isVisible = true

    internal var indicatorVisibility: Boolean = false


    private val mUnSelectedGradientDrawable: GradientDrawable
    private val mSelectedGradientDrawable: GradientDrawable

    private val mSelectedLayerDrawable: LayerDrawable
    private val mUnSelectedLayerDrawable: LayerDrawable

    private val mSelectedPadding_Left: Float
    private val mSelectedPadding_Right: Float
    private val mSelectedPadding_Top: Float
    private val mSelectedPadding_Bottom: Float

    private val mUnSelectedPadding_Left: Float
    private val mUnSelectedPadding_Right: Float
    private val mUnSelectedPadding_Top: Float
    private val mUnSelectedPadding_Bottom: Float

    /**
     * Put all the indicators into a ArrayList, so we can remove them easily.
     */
    private val mIndicators = ArrayList<ImageView>()

    /**
     * since we used a adapter wrapper, so we can't getCount directly from wrapper.
     *
     * @return
     */
    private val shouldDrawCount: Int
        get() = if (mRecyclerView?.adapter is PlutoAdapter<*, *>) {
            (mRecyclerView?.adapter as PlutoAdapter<*, *>).realCount
        } else {
            mRecyclerView?.adapter?.itemCount!!
        }

    private var mDataObserver: RecyclerView.AdapterDataObserver? = null

    init {

        val attributes = mContext.obtainStyledAttributes(attrs,
                R.styleable.PlutoIndicator, 0, 0)

        indicatorVisibility = attributes.getBoolean(R.styleable.PlutoIndicator_visibility,
                true)


        val shape = attributes.getInt(R.styleable.PlutoIndicator_shape,
                Shape.Oval.ordinal)
        var indicatorShape = Shape.Oval
        for (s in Shape.values()) {
            if (s.ordinal == shape) {
                indicatorShape = s
                break
            }
        }

        selectedIndicatorResId = attributes.getResourceId(R.styleable.PlutoIndicator_selected_drawable,
                0)
        unSelectedIndicatorResId = attributes.getResourceId(R.styleable.PlutoIndicator_unselected_drawable,
                0)

        val defaultSelectedColor = attributes.getColor(R.styleable.PlutoIndicator_selected_color,
                Color.rgb(255, 255, 255))
        val defaultUnSelectedColor = attributes.getColor(R.styleable.PlutoIndicator_unselected_color,
                Color.argb(33, 255, 255, 255))

        val defaultSelectedWidth = attributes.getDimension(R.styleable.PlutoIndicator_selected_width,
                pxFromDp(6f).toInt().toFloat())
        val defaultSelectedHeight = attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_selected_height,
                pxFromDp(6f).toInt()).toFloat()

        val defaultUnSelectedWidth = attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_unselected_width,
                pxFromDp(6f).toInt()).toFloat()
        val defaultUnSelectedHeight = attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_unselected_height,
                pxFromDp(6f).toInt()).toFloat()

        mSelectedGradientDrawable = GradientDrawable()
        mUnSelectedGradientDrawable = GradientDrawable()

        val padding_left = attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_padding_left,
                pxFromDp(3f).toInt()).toFloat()
        val padding_right = attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_padding_right,
                pxFromDp(3f).toInt()).toFloat()
        val padding_top = attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_padding_top,
                pxFromDp(0f).toInt()).toFloat()
        val padding_bottom = attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_padding_bottom, pxFromDp(0f).toInt()).toFloat()

        mSelectedPadding_Left = attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_selected_padding_left, padding_left.toInt()).toFloat()
        mSelectedPadding_Right = attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_selected_padding_right, padding_right.toInt()).toFloat()
        mSelectedPadding_Top = attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_selected_padding_top,
                padding_top.toInt()).toFloat()
        mSelectedPadding_Bottom = attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_selected_padding_bottom, padding_bottom.toInt()).toFloat()

        mUnSelectedPadding_Left = attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_unselected_padding_left, padding_left.toInt()).toFloat()
        mUnSelectedPadding_Right = attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_unselected_padding_right, padding_right.toInt()).toFloat()
        mUnSelectedPadding_Top = attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_unselected_padding_top, padding_top.toInt()).toFloat()
        mUnSelectedPadding_Bottom = attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_unselected_padding_bottom, padding_bottom.toInt()).toFloat()

        mSelectedLayerDrawable = LayerDrawable(arrayOf<Drawable>(mSelectedGradientDrawable))
        mUnSelectedLayerDrawable = LayerDrawable(arrayOf<Drawable>(mUnSelectedGradientDrawable))

        initObserver()

        setIndicatorDrawableRes(selectedIndicatorResId, unSelectedIndicatorResId)
        setDefaultIndicatorShape(indicatorShape)
        setDefaultSelectedIndicatorSize(defaultSelectedWidth, defaultSelectedHeight, Unit.Px)
        setDefaultUnselectedIndicatorSize(defaultUnSelectedWidth, defaultUnSelectedHeight,
                Unit.Px)
        setDefaultIndicatorColor(defaultSelectedColor, defaultUnSelectedColor)
        setVisibility(isVisible)
    }

    private fun initObserver() {
        mDataObserver = object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                redraw()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                redraw()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int,
                                            payload: Any?) {
                redraw()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                redraw()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                redraw()
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                redraw()
            }
        }
    }

    enum class Shape {
        Oval, Rectangle
    }

    /**
     * if you are using the default indicator, this method will help you to set the shape of
     * indicator, there are two kind of shapes you  can set, oval and rect.
     *
     * @param shape
     */
    fun setDefaultIndicatorShape(shape: Shape) {
        if (selectedIndicatorResId == 0) {
            if (shape == Shape.Oval) {
                mSelectedGradientDrawable.shape = GradientDrawable.OVAL
            } else {
                mSelectedGradientDrawable.shape = GradientDrawable.RECTANGLE
            }
        }
        if (unSelectedIndicatorResId == 0) {
            if (shape == Shape.Oval) {
                mUnSelectedGradientDrawable.shape = GradientDrawable.OVAL
            } else {
                mUnSelectedGradientDrawable.shape = GradientDrawable.RECTANGLE
            }
        }
        resetDrawable()
    }


    /**
     * Set Indicator style.
     *
     * @param selectedDrawable   page selected drawable
     * @param unselectedDrawable page unselected drawable
     */
    fun setIndicatorDrawableRes(@DrawableRes selectedDrawable: Int, @DrawableRes unselectedDrawable: Int) {
        selectedIndicatorResId = selectedDrawable
        unSelectedIndicatorResId = unselectedDrawable
        if (selectedDrawable == 0) {
            mSelectedDrawable = mSelectedLayerDrawable
        } else {
            mSelectedDrawable = mContext.resources.getDrawable(selectedIndicatorResId)
        }
        if (unselectedDrawable == 0) {
            mUnselectedDrawable = mUnSelectedLayerDrawable
        } else {
            mUnselectedDrawable = mContext.resources.getDrawable(unSelectedIndicatorResId)
        }

        resetDrawable()
    }

    /**
     * if you are using the default indicator , this method will help you to set the selected
     * status and
     * the unselected status color.
     *
     * @param selectedColor
     * @param unselectedColor
     */
    fun setDefaultIndicatorColor(selectedColor: Int, unselectedColor: Int) {
        if (selectedIndicatorResId == 0) {
            mSelectedGradientDrawable.setColor(selectedColor)
        }
        if (unSelectedIndicatorResId == 0) {
            mUnSelectedGradientDrawable.setColor(unselectedColor)
        }
        resetDrawable()
    }

    enum class Unit {
        DP, Px
    }

    fun setDefaultSelectedIndicatorSize(width: Float, height: Float, unit: Unit) {
        if (selectedIndicatorResId == 0) {
            var w = width
            var h = height
            if (unit == Unit.DP) {
                w = pxFromDp(width)
                h = pxFromDp(height)
            }
            mSelectedGradientDrawable.setSize(w.toInt(), h.toInt())
            resetDrawable()
        }
    }

    fun setDefaultUnselectedIndicatorSize(width: Float, height: Float, unit: Unit) {
        if (unSelectedIndicatorResId == 0) {
            var w = width
            var h = height
            if (unit == Unit.DP) {
                w = pxFromDp(width)
                h = pxFromDp(height)
            }
            mUnSelectedGradientDrawable.setSize(w.toInt(), h.toInt())
            resetDrawable()
        }
    }

    fun setDefaultIndicatorSize(width: Float, height: Float, unit: Unit) {
        setDefaultSelectedIndicatorSize(width, height, unit)
        setDefaultUnselectedIndicatorSize(width, height, unit)
    }

    private fun dpFromPx(px: Float): Float {
        return px / this.context.resources.displayMetrics.density
    }

    private fun pxFromDp(dp: Float): Float {
        return dp * this.context.resources.displayMetrics.density
    }

    /**
     * set the visibility of indicator.
     *
     * @param visibility
     */
    fun setVisibility(visibility: Boolean) {
        setVisibility(if (visibility) View.VISIBLE else View.INVISIBLE)
        resetDrawable()
    }

    /**
     * clear self means unregister the dataset observer and remove all the child views(indicators).
     */
    fun destroySelf() {
        if (mRecyclerView == null && mRecyclerView?.adapter == null) {
            return
        }
        snapOnScrollListener?.let {
            mRecyclerView?.removeOnScrollListener(it)
            snapOnScrollListener = null
        }
        val adapter = mRecyclerView?.adapter
        if (adapter != null && mDataObserver != null) {
            mDataObserver?.let {
                try {
                    adapter.unregisterAdapterDataObserver(it)
                } catch (ignore: Throwable) {
                }
                mDataObserver = null
            }
        }
        removeAllViews()
    }

    /**
     * bind indicator with RecyclerView.
     *
     * @param recyclerView
     */
    internal fun setRecyclerView(recyclerView: RecyclerView?, helper: SnapHelper) {
        mRecyclerView = recyclerView
        val adapter = mRecyclerView?.adapter
                ?: throw IllegalStateException("Viewpager does not have adapter instance")
        mRecyclerView = recyclerView
        if (mDataObserver == null)
            initObserver()
        try {
            adapter.registerAdapterDataObserver(mDataObserver!!)
        } catch (ignore: Throwable) {
        }
        snapOnScrollListener?.let {
            mRecyclerView?.removeOnScrollListener(it)
            snapOnScrollListener = null
        }
        initScrollListener(helper, adapter)
        snapOnScrollListener?.let {
            mRecyclerView?.addOnScrollListener(it)
        }

    }

    private fun initScrollListener(helper: SnapHelper, adapter: RecyclerView.Adapter<*>) {
        snapOnScrollListener = SnapOnScrollListener(helper, object : OnSnapPositionChangeListener {
            override fun onSnapPositionChange(position: Int) {
                var snapPositon = RecyclerView.NO_POSITION
                if (mItemCount == 0) {
                    return
                }
                if (adapter is PlutoAdapter<*, *>) {
                    if (adapter.realCount == 0)
                        return
                    snapPositon = position % adapter.realCount
                }

                setItemAsSelected(snapPositon - 1)


            }
        })
    }


    private fun resetDrawable() {
        for (i in mIndicators) {
            if (mPreviousSelectedIndicator != null && mPreviousSelectedIndicator == i) {
                i.setImageDrawable(mSelectedDrawable)
            } else {
                i.setImageDrawable(mUnselectedDrawable)
            }
        }
    }

    /**
     * redraw the indicators.
     */
    fun redraw() {
        mItemCount = shouldDrawCount
        mPreviousSelectedIndicator = null
        for (i in mIndicators) {
            removeView(i)
        }


        for (i in 0 until mItemCount) {
            val indicator = ImageView(mContext)
            indicator.setImageDrawable(mUnselectedDrawable)
            indicator.setPadding(mUnSelectedPadding_Left.toInt(),
                    mUnSelectedPadding_Top.toInt(),
                    mUnSelectedPadding_Right.toInt(),
                    mUnSelectedPadding_Bottom.toInt())
            addView(indicator)
            mIndicators.add(indicator)
        }
        setItemAsSelected(mPreviousSelectedPosition)
    }

    private fun setItemAsSelected(position: Int) {
        mPreviousSelectedIndicator?.let {
            it.setImageDrawable(mUnselectedDrawable)
            it.setPadding(
                    mUnSelectedPadding_Left.toInt(),
                    mUnSelectedPadding_Top.toInt(),
                    mUnSelectedPadding_Right.toInt(),
                    mUnSelectedPadding_Bottom.toInt()
            )
        }
        var currentSelected = getChildAt(position + 1)
        if (currentSelected != null) {
            currentSelected = getChildAt(position + 1) as ImageView
            currentSelected.setImageDrawable(mSelectedDrawable)
            currentSelected.setPadding(
                    mSelectedPadding_Left.toInt(),
                    mSelectedPadding_Top.toInt(),
                    mSelectedPadding_Right.toInt(),
                    mSelectedPadding_Bottom.toInt()
            )
            mPreviousSelectedIndicator = currentSelected

        }
        mPreviousSelectedPosition = position
    }

    override fun onDetachedFromWindow() {
        destroySelf()
        super.onDetachedFromWindow()
    }
}