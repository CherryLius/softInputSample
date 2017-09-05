package cherry.android.softinput.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import cherry.android.softinput.R;

/**
 * Created by Administrator on 2017/7/4.
 */

public class CircleIndicator extends ViewGroup {

    private static final String TAG = "CircleIndicator";

    private SparseArray<View> mIndicators;
    private int mIndicatorGap = 5;
    private int mIndicatorPadding = 10;
    private int mIndicatorWidth = 10;
    private int mIndicatorHeight = 10;
    @DrawableRes
    private int mIndicatorDrawableRes = R.drawable.ic_default_indicator;

    private int mSelectedPosition;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private IndicatorOnPagerChangeListener mPagerChangeListener;
    private AdapterChangeListener mAdapterChangeListener;
    private DataSetObserver mPagerAdapterObserver;

    public CircleIndicator(Context context) {
        this(context, null);
    }

    public CircleIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mIndicators = new SparseArray<>();
    }

    public void setGap(int gap) {
        mIndicatorGap = gap;
    }

    public void setIndicatorPadding(int padding) {
        mIndicatorPadding = padding;
    }

    public void setIndicatorWidth(int width) {
        mIndicatorWidth = width;
    }

    public void setIndicatorHeight(int height) {
        mIndicatorHeight = height;
    }

    public void setIndicatorDrawableRes(@DrawableRes int resId) {
        mIndicatorDrawableRes = resId;
    }

    private void setCount(int count) {
        final int oldSize = mIndicators.size();
        if (count == oldSize) return;
        final int delta = count - oldSize;
        fillIndicators(delta);
    }

    private void fillIndicators(int delta) {
        for (int i = 0; i < Math.abs(delta); i++) {
            if (delta > 0) {
                View view = new View(getContext());
                view.setBackgroundResource(mIndicatorDrawableRes);
                mIndicators.put(mIndicators.size(), view);
                addView(view, new LayoutParams(mIndicatorWidth, mIndicatorHeight));
            } else {
                View view = mIndicators.get(mIndicators.size() - 1);
                mIndicators.remove(mIndicators.size() - 1);
                removeView(view);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
        int indicatorWidth = mIndicatorWidth * childCount + mIndicatorGap * (childCount - 1) + 2 * mIndicatorPadding;
        int indicatorHeight = mIndicatorHeight + 2 * mIndicatorPadding;
        int width = Math.max(indicatorWidth + getPaddingLeft() + getPaddingRight(), getSuggestedMinimumWidth());
        int height = Math.max(indicatorHeight + getPaddingTop() + getPaddingBottom(), getSuggestedMinimumHeight());
        setMeasuredDimension(measureDimen(width, widthMeasureSpec),
                measureDimen(height, heightMeasureSpec));
    }

    private static int measureDimen(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(size, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int parentWidth = getMeasuredWidth();
        final int parentHeight = getMeasuredHeight();
        final int indicatorCount = mIndicators.size();
        int totalGap = mIndicatorGap * (indicatorCount - 1);
        int totalWidth = mIndicatorWidth * indicatorCount + totalGap + 2 * mIndicatorPadding;
        int left = (parentWidth - totalWidth) / 2 + mIndicatorPadding;
        int top = (parentHeight - mIndicatorHeight) / 2;
        Log.d(TAG, "size=" + indicatorCount);
        Log.d(TAG, "left=" + left + ",top=" + top);
        Log.e(TAG, "[parentWidth]=" + parentWidth + ",[parentHeight]=" + parentHeight);

        for (int i = 0; i < indicatorCount; i++) {
            final View child = mIndicators.get(i);
            int realLeft = left + ((mIndicatorGap + mIndicatorWidth) * i);
            Log.e(TAG, "realLeft=" + realLeft);
            child.layout(realLeft, top, realLeft + mIndicatorWidth, top + mIndicatorHeight);
        }
    }

    private void onPageSelected(int position) {
        Log.d(TAG, "position=" + position + ", oldPosition=" + mSelectedPosition + ",mIndicators =" + mIndicators.size());
        if (mIndicators.size() == 0)
            return;
        if (mSelectedPosition >= 0 && mSelectedPosition != position
                && mSelectedPosition < mIndicators.size()) {
            mIndicators.get(mSelectedPosition).setSelected(false);
        }
        mSelectedPosition = position;
        mIndicators.get(position).setSelected(true);
    }

    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        setupWithViewPager(viewPager, true);
    }

    public void setupWithViewPager(@Nullable ViewPager viewPager, boolean autoRefresh) {
        if (mViewPager != null) {
            if (mPagerChangeListener != null) {
                mViewPager.removeOnPageChangeListener(mPagerChangeListener);
            }
            if (mAdapterChangeListener != null) {
                mViewPager.removeOnAdapterChangeListener(mAdapterChangeListener);
            }
        }
        if (viewPager != null) {
            mViewPager = viewPager;

            if (mPagerChangeListener == null) {
                mPagerChangeListener = new IndicatorOnPagerChangeListener(this);
            }
            mPagerChangeListener.reset();
            viewPager.addOnPageChangeListener(mPagerChangeListener);

            final PagerAdapter adapter = viewPager.getAdapter();
            if (adapter != null) {
                setPagerAdapter(adapter, autoRefresh);
            }
            if (mAdapterChangeListener == null) {
                mAdapterChangeListener = new AdapterChangeListener();
            }
            mAdapterChangeListener.setAutoRefresh(autoRefresh);
            viewPager.addOnAdapterChangeListener(mAdapterChangeListener);
            onPageSelected(viewPager.getCurrentItem());
        } else {
            setPagerAdapter(null, false);
        }
    }

    void setPagerAdapter(@Nullable final PagerAdapter adapter, final boolean addObserver) {
        if (mPagerAdapter != null && mPagerAdapterObserver != null) {
            mPagerAdapter.unregisterDataSetObserver(mPagerAdapterObserver);
        }
        mPagerAdapter = adapter;
        if (addObserver && adapter != null) {
            if (mPagerAdapterObserver == null)
                mPagerAdapterObserver = new PagerAdapterObserver();
            adapter.registerDataSetObserver(mPagerAdapterObserver);
        }
        populateFromPagerAdapter();
    }

    void populateFromPagerAdapter() {
        if (mPagerAdapter != null) {
            final int adapterCount = mPagerAdapter.getCount();
            setCount(adapterCount);
            if (mViewPager != null && adapterCount > 0) {
                onPageSelected(mViewPager.getCurrentItem());
            }
        }
    }

    private static class IndicatorOnPagerChangeListener implements ViewPager.OnPageChangeListener {

        private WeakReference<CircleIndicator> mIndicatorRef;
        private int mPreviousScrollState;
        private int mScrollState;

        IndicatorOnPagerChangeListener(CircleIndicator indicator) {
            mIndicatorRef = new WeakReference<>(indicator);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            final CircleIndicator indicator = mIndicatorRef.get();
            if (indicator != null) {
                indicator.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mPreviousScrollState = mScrollState;
            mScrollState = state;
        }

        public void reset() {
            mPreviousScrollState = mScrollState = ViewPager.SCROLL_STATE_IDLE;
        }
    }

    private class AdapterChangeListener implements ViewPager.OnAdapterChangeListener {

        private boolean mAutoRefresh;

        @Override
        public void onAdapterChanged(@NonNull ViewPager viewPager,
                                     @Nullable PagerAdapter oldAdapter,
                                     @Nullable PagerAdapter newAdapter) {
            if (mViewPager == viewPager) {
                setPagerAdapter(newAdapter, mAutoRefresh);
            }
        }

        void setAutoRefresh(boolean autoRefresh) {
            mAutoRefresh = autoRefresh;
        }
    }

    private class PagerAdapterObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            populateFromPagerAdapter();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            populateFromPagerAdapter();
        }
    }
}
