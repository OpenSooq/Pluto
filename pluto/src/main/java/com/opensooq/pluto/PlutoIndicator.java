package com.opensooq.pluto;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.opensooq.pluto.base.PlutoAdapter;
import com.opensooq.pluto.listeners.SnapOnScrollListener;

import java.util.ArrayList;

/**
 * Created by Omar Altamimi on 28,April,2019
 */

public class PlutoIndicator extends LinearLayout {

    private Context mContext;

    private RecyclerView mRecyclerView;

    /**
     * Variable to remember the previous selected indicator.
     */
    private ImageView mPreviousSelectedIndicator;

    /**
     * Previous selected indicator position.
     */
    private int mPreviousSelectedPosition;

    /**
     * Custom selected indicator style resource id.
     */
    private int mUserSetUnSelectedIndicatorResId;


    /**
     * Custom unselected indicator style resource id.
     */
    private int mUserSetSelectedIndicatorResId;

    private Drawable mSelectedDrawable;
    private Drawable mUnselectedDrawable;
    SnapOnScrollListener snapOnScrollListener;
    private int mItemCount = 0;

    private boolean isVisible = true;

    boolean indicatorVisibility;


    private GradientDrawable mUnSelectedGradientDrawable;
    private GradientDrawable mSelectedGradientDrawable;

    private LayerDrawable mSelectedLayerDrawable;
    private LayerDrawable mUnSelectedLayerDrawable;

    private float mSelectedPadding_Left;
    private float mSelectedPadding_Right;
    private float mSelectedPadding_Top;
    private float mSelectedPadding_Bottom;

    private float mUnSelectedPadding_Left;
    private float mUnSelectedPadding_Right;
    private float mUnSelectedPadding_Top;
    private float mUnSelectedPadding_Bottom;

    /**
     * Put all the indicators into a ArrayList, so we can remove them easily.
     */
    private ArrayList<ImageView> mIndicators = new ArrayList<ImageView>();


    public PlutoIndicator(Context context) {
        this(context, null);
    }

    public PlutoIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        final TypedArray attributes = context.obtainStyledAttributes(attrs,
                R.styleable.PlutoIndicator, 0, 0);

        indicatorVisibility = attributes.getBoolean(R.styleable.PlutoIndicator_visibility,
                true);


        int shape = attributes.getInt(R.styleable.PlutoIndicator_shape,
                Shape.Oval.ordinal());
        Shape indicatorShape = Shape.Oval;
        for (Shape s : Shape.values()) {
            if (s.ordinal() == shape) {
                indicatorShape = s;
                break;
            }
        }

        mUserSetSelectedIndicatorResId =
                attributes.getResourceId(R.styleable.PlutoIndicator_selected_drawable,
                        0);
        mUserSetUnSelectedIndicatorResId =
                attributes.getResourceId(R.styleable.PlutoIndicator_unselected_drawable,
                        0);

        int defaultSelectedColor = attributes.getColor(R.styleable.PlutoIndicator_selected_color,
                Color.rgb(255, 255, 255));
        int defaultUnSelectedColor =
                attributes.getColor(R.styleable.PlutoIndicator_unselected_color,
                        Color.argb(33, 255, 255, 255));

        float defaultSelectedWidth =
                attributes.getDimension(R.styleable.PlutoIndicator_selected_width,
                        (int) pxFromDp(6));
        float defaultSelectedHeight =
                attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_selected_height,
                        (int) pxFromDp(6));

        float defaultUnSelectedWidth =
                attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_unselected_width,
                        (int) pxFromDp(6));
        float defaultUnSelectedHeight =
                attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_unselected_height,
                        (int) pxFromDp(6));

        mSelectedGradientDrawable = new GradientDrawable();
        mUnSelectedGradientDrawable = new GradientDrawable();

        float padding_left =
                attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_padding_left,
                        (int) pxFromDp(3));
        float padding_right =
                attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_padding_right,
                        (int) pxFromDp(3));
        float padding_top = attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_padding_top,
                (int) pxFromDp(0));
        float padding_bottom =
                attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_padding_bottom
                        , (int) pxFromDp(0));

        mSelectedPadding_Left =
                attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_selected_padding_left
                        , (int) padding_left);
        mSelectedPadding_Right =
                attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_selected_padding_right, (int) padding_right);
        mSelectedPadding_Top =
                attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_selected_padding_top,
                        (int) padding_top);
        mSelectedPadding_Bottom =
                attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_selected_padding_bottom, (int) padding_bottom);

        mUnSelectedPadding_Left =
                attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_unselected_padding_left, (int) padding_left);
        mUnSelectedPadding_Right =
                attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_unselected_padding_right, (int) padding_right);
        mUnSelectedPadding_Top =
                attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_unselected_padding_top, (int) padding_top);
        mUnSelectedPadding_Bottom =
                attributes.getDimensionPixelSize(R.styleable.PlutoIndicator_unselected_padding_bottom, (int) padding_bottom);

        mSelectedLayerDrawable = new LayerDrawable(new Drawable[]{mSelectedGradientDrawable});
        mUnSelectedLayerDrawable = new LayerDrawable(new Drawable[]{mUnSelectedGradientDrawable});

        initObserver();

        setIndicatorDrawableRes(mUserSetSelectedIndicatorResId, mUserSetUnSelectedIndicatorResId);
        setDefaultIndicatorShape(indicatorShape);
        setDefaultSelectedIndicatorSize(defaultSelectedWidth, defaultSelectedHeight, Unit.Px);
        setDefaultUnselectedIndicatorSize(defaultUnSelectedWidth, defaultUnSelectedHeight,
                Unit.Px);
        setDefaultIndicatorColor(defaultSelectedColor, defaultUnSelectedColor);
        setVisibility(isVisible);
    }

    private void initObserver() {
        mDataObserver =
                new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        redraw();
                    }

                    @Override
                    public void onItemRangeChanged(int positionStart, int itemCount) {
                        redraw();
                    }

                    @Override
                    public void onItemRangeChanged(int positionStart, int itemCount,
                                                   @Nullable Object payload) {
                        redraw();
                    }

                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        redraw();
                    }

                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {
                        redraw();
                    }

                    @Override
                    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                        redraw();
                    }
                };
    }

    public enum Shape {
        Oval, Rectangle
    }

    /**
     * if you are using the default indicator, this method will help you to set the shape of
     * indicator, there are two kind of shapes you  can set, oval and rect.
     *
     * @param shape
     */
    public void setDefaultIndicatorShape(Shape shape) {
        if (mUserSetSelectedIndicatorResId == 0) {
            if (shape == Shape.Oval) {
                mSelectedGradientDrawable.setShape(GradientDrawable.OVAL);
            } else {
                mSelectedGradientDrawable.setShape(GradientDrawable.RECTANGLE);
            }
        }
        if (mUserSetUnSelectedIndicatorResId == 0) {
            if (shape == Shape.Oval) {
                mUnSelectedGradientDrawable.setShape(GradientDrawable.OVAL);
            } else {
                mUnSelectedGradientDrawable.setShape(GradientDrawable.RECTANGLE);
            }
        }
        resetDrawable();
    }


    /**
     * Set Indicator style.
     *
     * @param selectedDrawable   page selected drawable
     * @param unselectedDrawable page unselected drawable
     */
    public void setIndicatorDrawableRes(@DrawableRes int selectedDrawable, @DrawableRes int unselectedDrawable) {
        mUserSetSelectedIndicatorResId = selectedDrawable;
        mUserSetUnSelectedIndicatorResId = unselectedDrawable;
        if (selectedDrawable == 0) {
            mSelectedDrawable = mSelectedLayerDrawable;
        } else {
            mSelectedDrawable = mContext.getResources().getDrawable(mUserSetSelectedIndicatorResId);
        }
        if (unselectedDrawable == 0) {
            mUnselectedDrawable = mUnSelectedLayerDrawable;
        } else {
            mUnselectedDrawable =
                    mContext.getResources().getDrawable(mUserSetUnSelectedIndicatorResId);
        }

        resetDrawable();
    }

    /**
     * if you are using the default indicator , this method will help you to set the selected
     * status and
     * the unselected status color.
     *
     * @param selectedColor
     * @param unselectedColor
     */
    public void setDefaultIndicatorColor(int selectedColor, int unselectedColor) {
        if (mUserSetSelectedIndicatorResId == 0) {
            mSelectedGradientDrawable.setColor(selectedColor);
        }
        if (mUserSetUnSelectedIndicatorResId == 0) {
            mUnSelectedGradientDrawable.setColor(unselectedColor);
        }
        resetDrawable();
    }

    public enum Unit {
        DP, Px
    }

    public void setDefaultSelectedIndicatorSize(float width, float height, Unit unit) {
        if (mUserSetSelectedIndicatorResId == 0) {
            float w = width;
            float h = height;
            if (unit == Unit.DP) {
                w = pxFromDp(width);
                h = pxFromDp(height);
            }
            mSelectedGradientDrawable.setSize((int) w, (int) h);
            resetDrawable();
        }
    }

    public void setDefaultUnselectedIndicatorSize(float width, float height, Unit unit) {
        if (mUserSetUnSelectedIndicatorResId == 0) {
            float w = width;
            float h = height;
            if (unit == Unit.DP) {
                w = pxFromDp(width);
                h = pxFromDp(height);
            }
            mUnSelectedGradientDrawable.setSize((int) w, (int) h);
            resetDrawable();
        }
    }

    public void setDefaultIndicatorSize(float width, float height, Unit unit) {
        setDefaultSelectedIndicatorSize(width, height, unit);
        setDefaultUnselectedIndicatorSize(width, height, unit);
    }

    private float dpFromPx(float px) {
        return px / this.getContext().getResources().getDisplayMetrics().density;
    }

    private float pxFromDp(float dp) {
        return dp * this.getContext().getResources().getDisplayMetrics().density;
    }

    /**
     * set the visibility of indicator.
     *
     * @param visibility
     */
    public void setVisibility(boolean visibility) {
        setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
        resetDrawable();
    }

    /**
     * clear self means unregister the dataset observer and remove all the child views(indicators).
     */
    public void destroySelf() {
        if (mRecyclerView == null || mRecyclerView.getAdapter() == null) {
            return;
        }
        if (snapOnScrollListener != null) {
            mRecyclerView.removeOnScrollListener(snapOnScrollListener);
            snapOnScrollListener = null;
        }
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter != null && mDataObserver != null) {
            adapter.unregisterAdapterDataObserver(mDataObserver);
            mDataObserver = null;
        }
        removeAllViews();
    }

    /**
     * bind indicator with RecyclerView.
     *
     * @param recyclerView
     */
     void setRecyclerView(RecyclerView recyclerView, SnapHelper helper) {
        mRecyclerView = recyclerView;
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("Viewpager does not have adapter instance");

        }
        mRecyclerView = recyclerView;
        if (mDataObserver == null)
            initObserver();
        adapter.registerAdapterDataObserver(mDataObserver);
        if (snapOnScrollListener != null) {
            mRecyclerView.removeOnScrollListener(snapOnScrollListener);
        }
        initScrollListener(helper, adapter);
        mRecyclerView.addOnScrollListener(snapOnScrollListener);
    }

    private void initScrollListener(SnapHelper helper, RecyclerView.Adapter adapter) {
        snapOnScrollListener = new SnapOnScrollListener(helper, position -> {
            if (mItemCount == 0) {
                return;
            }
            if (adapter instanceof PlutoAdapter) {
                if (((PlutoAdapter) adapter).getRealCount() == 0)
                    return;
                position = position % ((PlutoAdapter) adapter).getRealCount();
            }

            setItemAsSelected(position - 1);

        });
    }


    private void resetDrawable() {
        for (View i : mIndicators) {
            if (mPreviousSelectedIndicator != null && mPreviousSelectedIndicator.equals(i)) {
                ((ImageView) i).setImageDrawable(mSelectedDrawable);
            } else {
                ((ImageView) i).setImageDrawable(mUnselectedDrawable);
            }
        }
    }

    /**
     * redraw the indicators.
     */
    public void redraw() {
        mItemCount = getShouldDrawCount();
        mPreviousSelectedIndicator = null;
        for (View i : mIndicators) {
            removeView(i);
        }


        for (int i = 0; i < mItemCount; i++) {
            ImageView indicator = new ImageView(mContext);
            indicator.setImageDrawable(mUnselectedDrawable);
            indicator.setPadding((int) mUnSelectedPadding_Left,
                    (int) mUnSelectedPadding_Top,
                    (int) mUnSelectedPadding_Right,
                    (int) mUnSelectedPadding_Bottom);
            addView(indicator);
            mIndicators.add(indicator);
        }
        setItemAsSelected(mPreviousSelectedPosition);
    }

    /**
     * since we used a adapter wrapper, so we can't getCount directly from wrapper.
     *
     * @return
     */
    private int getShouldDrawCount() {
        if (mRecyclerView.getAdapter() instanceof PlutoAdapter) {
            return ((PlutoAdapter) mRecyclerView.getAdapter()).getRealCount();
        } else {
            return mRecyclerView.getAdapter().getItemCount();
        }
    }

    private RecyclerView.AdapterDataObserver mDataObserver;

    private void setItemAsSelected(int position) {
        if (mPreviousSelectedIndicator != null) {
            mPreviousSelectedIndicator.setImageDrawable(mUnselectedDrawable);
            mPreviousSelectedIndicator.setPadding(
                    (int) mUnSelectedPadding_Left,
                    (int) mUnSelectedPadding_Top,
                    (int) mUnSelectedPadding_Right,
                    (int) mUnSelectedPadding_Bottom
            );
        }
        ImageView currentSelected = (ImageView) getChildAt(position + 1);
        if (currentSelected != null) {
            currentSelected.setImageDrawable(mSelectedDrawable);
            currentSelected.setPadding(
                    (int) mSelectedPadding_Left,
                    (int) mSelectedPadding_Top,
                    (int) mSelectedPadding_Right,
                    (int) mSelectedPadding_Bottom
            );
            mPreviousSelectedIndicator = currentSelected;
        }
        mPreviousSelectedPosition = position;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public int getSelectedIndicatorResId() {
        return mUserSetSelectedIndicatorResId;
    }

    public int getUnSelectedIndicatorResId() {
        return mUserSetUnSelectedIndicatorResId;
    }

    @Override
    protected void onDetachedFromWindow() {
        destroySelf();
        super.onDetachedFromWindow();
    }
}