package cherry.android.softinput;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import cherry.android.softinput.util.DensityUtils;

/**
 * Created by ROOT on 2017/9/1.
 */

public class EmotionKeyboard implements View.OnClickListener {
    private Activity mActivity;
    private InputMethodManager mInputMethodManager;//软键盘管理类
    private View mEmotionLayout;//表情面板
    private EditText mEditText;//输入编辑框
    private View mContentView;//内容布局view,即除了表情布局或者软键盘布局以外的布局，用于固定bar的高度，防止跳闪

    public interface OnClickListener {
        boolean onClick(@NonNull View view);
    }

    private EmotionKeyboard(@NonNull Activity activity) {
        this.mActivity = activity;
        this.mInputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public static EmotionKeyboard with(@NonNull Activity activity) {
        return new EmotionKeyboard(activity);
    }

    /**
     * 绑定内容view，此view用于固定bar的高度，防止跳闪
     */
    public EmotionKeyboard bindContent(@NonNull View contentView) {
        this.mContentView = contentView;
        return this;
    }

    /**
     * 绑定编辑框
     */

    public EmotionKeyboard bindEditText(@NonNull EditText editText) {
        this.mEditText = editText;
        this.mEditText.requestFocus();
        this.mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP
                        && mEmotionLayout.isShown()) {
                    lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                    hideEmotionLayout(true);//隐藏表情布局，显示软件盘
                    unlockContentHeight();//软件盘显示后，释放内容高度
                }
                return false;
            }
        });
        return this;
    }

    /**
     * 绑定表情按钮（可以有多个表情按钮）
     *
     * @param emotionButton
     * @return
     */

    public EmotionKeyboard bindEmotionButton(View... emotionButton) {
        return bindEmotionButton(null, emotionButton);
    }

    private OnClickListener mOnClickListener;

    public EmotionKeyboard bindEmotionButton(OnClickListener listener, View... emotionButton) {
        this.mOnClickListener = listener;
        for (View view : emotionButton) {
            view.setOnClickListener(this);
        }
        return this;
    }

    /**
     * 设置表情内容布局
     *
     * @param emotionLayout
     * @return
     */
    public EmotionKeyboard setEmotionLayout(@NonNull View emotionLayout) {
        mEmotionLayout = emotionLayout;
        mEmotionLayout.setVisibility(View.GONE);
        return this;
    }

    public void build() {
        //设置软件盘的模式：SOFT_INPUT_ADJUST_RESIZE  这个属性表示Activity的主窗口总是会被调整大小，从而保证软键盘显示空间。
        //从而方便我们计算软件盘的高度
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //隐藏软件盘
        hideSoftInput();
    }

    @Override
    public void onClick(View view) {
        if (this.mOnClickListener != null && this.mOnClickListener.onClick(view)) {
            return;
        }
        if (mEmotionLayout == null) {
            return;
        }
        if (mEmotionLayout.isShown()) {
            lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
            hideEmotionLayout(true);//隐藏表情布局，显示软件盘
            unlockContentHeight();//软件盘显示后，释放内容高度
        } else {
            if (isSoftInputShown()) {//同上
                lockContentHeight();
                showEmotionLayout();
                unlockContentHeight();
            } else {
                showEmotionLayout();//两者都没显示，直接显示表情布局
            }
        }
    }

    /**
     * 点击返回键时先隐藏表情布局
     *
     * @return
     */
    public boolean interceptBackPress() {
        if (mEmotionLayout.isShown()) {
            hideEmotionLayout(false);
            return true;
        }
        return false;
    }

    private void showEmotionLayout() {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = (int) DensityUtils.dp2px(mActivity, 277);
        }
        hideSoftInput();
        mEmotionLayout.getLayoutParams().height = softInputHeight;
        mEmotionLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏表情布局
     *
     * @param showSoftInput 是否显示软件盘
     */
    private void hideEmotionLayout(boolean showSoftInput) {
        if (mEmotionLayout.isShown()) {
            mEmotionLayout.setVisibility(View.GONE);
            if (showSoftInput) {
                showSoftInput();
            }
        }
    }

    /**
     * 锁定内容高度，防止跳闪
     */
    private void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        params.height = mContentView.getHeight();
        params.weight = 0.0f;
    }

    /**
     * 释放被锁定的内容高度
     */
    private void unlockContentHeight() {
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
                params.weight = 1.0f;
            }
        }, 200);
    }

    /**
     * 编辑框获取焦点，并显示软件盘
     */
    public void showSoftInput() {
        mEditText.requestFocus();
        mEditText.post(new Runnable() {
            @Override
            public void run() {
                mInputMethodManager.showSoftInput(mEditText, 0);
            }
        });
    }

    /**
     * 隐藏软件盘
     */
    public void hideSoftInput() {
        mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * 是否显示软件盘
     */
    public boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    /**
     * 获取软件盘的高度
     */
    private int getSupportSoftInputHeight() {
        Rect rect = new Rect();
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //获取屏幕高度
        final int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        //计算软件盘的高度
        int softInputHeight = screenHeight - rect.bottom;
        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            softInputHeight -= getNavigationBarHeight(mActivity);
        }
        return softInputHeight;
    }

    private static int getNavigationBarHeight(@NonNull Context context) {
        final int showNavi = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (showNavi != 0) {
            final int identifier = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            final int height = context.getResources().getDimensionPixelSize(identifier);
            return height;
        }
        return 0;
    }
}
