package cherry.android.softinput.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cherry.android.softinput.EmotionProvider;
import cherry.android.softinput.OnEmotionSelectedListener;
import cherry.android.softinput.R;
import cherry.android.softinput.Values;
import cherry.android.softinput.adapter.EmotionPagerAdapter;
import cherry.android.softinput.emoji.EmojiManager;
import cherry.android.softinput.graphic.GraphicManager;
import cherry.android.softinput.util.DensityUtils;
import cherry.android.softinput.util.SizeHelper;

/**
 * Created by ROOT on 2017/9/1.
 */

public class EmotionLayout extends LinearLayout implements View.OnClickListener {

    private ViewPager mViewPager;
    private CircleIndicator mIndicator;
    private LinearLayout mTabContainer;
    private EmotionPagerAdapter mPagerAdapter;

    private EditText mAttachEditText;
    private OnEmotionSelectedListener mEmotionSelectedListener;
    private int mLastTab = -1;
    private List<EmotionProvider> mList = new ArrayList<>();

    public EmotionLayout(@NonNull Context context) {
        this(context, null);
    }

    public EmotionLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmotionLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (GraphicManager.get().isAssetCopyComplete()) {
            initEmotion();
        } else {
            GraphicManager.get().setAssetsCopyListener(new GraphicManager.AssetsCopyListener() {
                @Override
                public void onComplete() {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            init();
                            initIndicator();
                            initEmotion();
                            initTabs();
                            initListener();
                            selectTab(0);
                        }
                    });
                }
            });
        }
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.layout_emotion, this);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mIndicator = (CircleIndicator) findViewById(R.id.indicator);
        mTabContainer = (LinearLayout) findViewById(R.id.tabs);
        mViewPager.setOverScrollMode(OVER_SCROLL_NEVER);

    }

    private void initEmotion() {
        mList.clear();
        mList.add(EmojiManager.getEmoji(Values.CATEGORY_FACIAL));
        mList.add(EmojiManager.getEmoji(Values.CATEGORY_EMOJI));
        List<? extends EmotionProvider> graphics = GraphicManager.get().getProvider();
        if (graphics != null) {
            mList.addAll(graphics);
        }
    }

    private void initTabs() {
        mTabContainer.removeAllViews();
        for (int i = 0; i < mList.size(); i++) {
            EmotionTab emojiTab = new EmotionTab(getContext(), mList.get(i).getIndicator());
            emojiTab.setBackgroundResource(R.drawable.ic_tab_state_bg);
            mTabContainer.addView(emojiTab);
        }
    }

    private void initIndicator() {
        mIndicator.setIndicatorPadding((int) DensityUtils.dp2px(getContext(), 10));
        mIndicator.setGap((int) DensityUtils.dp2px(getContext(), 5));
        mIndicator.setupWithViewPager(mViewPager);
    }

    private void initListener() {
        final int childCount = mTabContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = mTabContainer.getChildAt(i);
            child.setOnClickListener(this);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
        initIndicator();
        SizeHelper.resolveParentSize(w, h);
        SizeHelper.resolveTabAndIndicatorHeight((int) DensityUtils.dp2px(getContext(), 35), mIndicator.getMeasuredHeight());
        initTabs();
        initListener();
        selectTab(0);
    }

    public void attachEditText(@Nullable EditText editText) {
        this.mAttachEditText = editText;
        if (this.mPagerAdapter != null) {
            this.mPagerAdapter.attachEditText(editText);
        }
    }

    public void setOnEmotionSelectedListener(@NonNull OnEmotionSelectedListener listener) {
        this.mEmotionSelectedListener = listener;
        if (this.mPagerAdapter != null) {
            this.mPagerAdapter.setOnEmotionSelectedListener(listener);
        }
    }

    @Override
    public void onClick(View view) {
        final int index = mTabContainer.indexOfChild(view);
        if (mLastTab == index)
            return;
        selectTab(index);
    }

    private void selectTab(int position) {
        if (mTabContainer.getChildCount() == 0)
            return;
        if (mLastTab >= 0 && mLastTab < mTabContainer.getChildCount()) {
            mTabContainer.getChildAt(mLastTab).setSelected(false);
        }
        mLastTab = position;
        mTabContainer.getChildAt(position).setSelected(true);
        EmotionProvider provider = mList.get(position);
        mPagerAdapter = new EmotionPagerAdapter(provider);
        mPagerAdapter.attachEditText(this.mAttachEditText);
        mPagerAdapter.setOnEmotionSelectedListener(this.mEmotionSelectedListener);
        mViewPager.setAdapter(mPagerAdapter);
    }
}
