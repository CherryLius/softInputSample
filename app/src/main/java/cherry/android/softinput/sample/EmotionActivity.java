package cherry.android.softinput.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import cherry.android.softinput.EmotionKeyboard;
import cherry.android.softinput.OnEmotionSelectedListener;
import cherry.android.softinput.view.EmotionLayout;

/**
 * Created by ROOT on 2017/9/1.
 */

public class EmotionActivity extends AppCompatActivity implements EmotionKeyboard.OnClickListener {

    LinearLayout mLlContent;
    FrameLayout mFlEmotionView;
    EmotionLayout emotionLayout;
    EditText mEtContent;
    EmotionKeyboard emotionKeyboard;
    ImageView mIvEmo;
    ImageView mIvMore;
    LinearLayout mLlMore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_input);
        emotionLayout = (EmotionLayout) findViewById(R.id.elEmotion);
        mEtContent = (EditText) findViewById(R.id.etContent);
        mLlContent = (LinearLayout) findViewById(R.id.llContent);
        mIvEmo = (ImageView) findViewById(R.id.ivEmo);
        mIvMore = (ImageView) findViewById(R.id.ivMore);
        mFlEmotionView = (FrameLayout) findViewById(R.id.flEmotionView);
        mLlMore = (LinearLayout) findViewById(R.id.llMore);

        emotionKeyboard = EmotionKeyboard.with(this);
        emotionKeyboard
                .bindEditText(mEtContent)
                .bindContent(mLlContent)
                .bindEmotionButton(this, mIvEmo, mIvMore)
                .setEmotionLayout(mFlEmotionView)
                .build();
        emotionLayout.attachEditText(mEtContent);
        emotionLayout.setOnEmotionSelectedListener(new OnEmotionSelectedListener() {
            @Override
            public void onEmojiSelected(String category, String description) {
                Toast.makeText(getApplicationContext(), category + "\n" + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGraphicSelected(String category, String title, String path) {
                Toast.makeText(getApplicationContext(), category + "\n" + path, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEtContent.clearFocus();
    }

    @Override
    public void onBackPressed() {
        if (emotionLayout.isShown()) {
            emotionKeyboard.interceptBackPress();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onClick(View view) {
        switch (view.getId()) {
            case R.id.ivEmo: {
                boolean intercept = mLlMore.isShown() && !emotionLayout.isShown();
                showEmotionLayout();
                hideMoreLayout();
                return intercept;
            }
            case R.id.ivMore: {
                boolean intercept = !mLlMore.isShown() && emotionLayout.isShown();
                showMoreLayout();
                hideEmotionLayout();
                return intercept;
            }
        }
        return false;
    }

    private void showMoreLayout() {
        mLlMore.setVisibility(View.VISIBLE);
    }

    private void hideEmotionLayout() {
        emotionLayout.setVisibility(View.GONE);
    }

    private void showEmotionLayout() {
        emotionLayout.setVisibility(View.VISIBLE);
    }

    private void hideMoreLayout() {
        mLlMore.setVisibility(View.GONE);
    }
}
