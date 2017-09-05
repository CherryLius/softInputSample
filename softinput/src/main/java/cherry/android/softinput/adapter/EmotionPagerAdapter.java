package cherry.android.softinput.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;

import cherry.android.softinput.EmotionProvider;
import cherry.android.softinput.OnEmotionSelectedListener;
import cherry.android.softinput.Values;
import cherry.android.softinput.emoji.EmojiProvider;
import cherry.android.softinput.graphic.GraphicProvider;
import cherry.android.softinput.model.EmojiItem;
import cherry.android.softinput.model.GraphicItem;
import cherry.android.softinput.util.EmotionUtils;

/**
 * Created by ROOT on 2017/9/1.
 */

public class EmotionPagerAdapter extends PagerAdapter {

    private int mPageCount;
    private EditText mAttachEditText;
    private OnEmotionSelectedListener mEmotionSelectedListener;
    private EmotionProvider mProvider;

    public EmotionPagerAdapter(EmotionProvider provider) {
        this.mProvider = provider;
        if (!isGraphic()) {
            mPageCount = (int) Math.ceil(mProvider.getCount() / (float) Values.EMOJI_PER_PAGE);
        } else {
            mPageCount = (int) Math.ceil(mProvider.getCount() / (float) Values.GRAPHIC_PER_PAGE);
        }
    }

    public void attachEditText(@Nullable EditText attachEditText) {
        this.mAttachEditText = attachEditText;
    }

    @Override
    public int getCount() {
        return mPageCount == 0 ? 1 : mPageCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Context context = container.getContext();
        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setGravity(Gravity.CENTER);

        GridView gridView = new GridView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        gridView.setLayoutParams(params);
        gridView.setGravity(Gravity.CENTER);
        gridView.setOnItemClickListener(mItemClickListener);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setTag(position);
        if (isGraphic()) {
            gridView.setNumColumns(Values.GRAPHIC_COLUMNS);
            gridView.setAdapter(new GraphicAdapter(container.getContext(), (GraphicProvider) mProvider, position * Values.GRAPHIC_PER_PAGE));
        } else {
            gridView.setNumColumns(Values.EMOJI_COLUMNS);
            gridView.setAdapter(new EmojiAdapter(container.getContext(), (EmojiProvider) mProvider, position * Values.EMOJI_PER_PAGE));
        }

        relativeLayout.addView(gridView);
        container.addView(relativeLayout);
        return relativeLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            final int page = (int) adapterView.getTag();
            if (mProvider instanceof EmojiProvider) {
                onEmojiItemClick((EmojiProvider) mProvider, page, position);
            } else {
                onGraphicItemClick((GraphicProvider) mProvider, page, position);
            }
        }
    };

    private void onEmojiItemClick(EmojiProvider provider, int page, int position) {
        final int index = page * Values.EMOJI_PER_PAGE + position;
        final int count = provider.getCount();
        if (position == Values.EMOJI_PER_PAGE || index >= count) {
            onEmojiSelected(Values.KEY_DEL);
        } else {
            EmojiItem item = provider.getValue(index);
            onEmojiSelected(item.getDescription());
        }
    }

    private void onEmojiSelected(String description) {
        if (this.mEmotionSelectedListener != null) {
            this.mEmotionSelectedListener.onEmojiSelected(mProvider.getEmotionCategory(), description);
        }
        if (mAttachEditText == null)
            return;
        Editable editable = mAttachEditText.getEditableText();
        if (Values.KEY_DEL.equals(description)) {
            mAttachEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        } else {
            int start = mAttachEditText.getSelectionStart();
            int end = mAttachEditText.getSelectionEnd();
            start = start < 0 ? 0 : start;
            end = end < 0 ? 0 : end;
            editable.replace(start, end, description);

            int editEnd = mAttachEditText.getSelectionEnd();
            Drawable drawable = EmotionUtils.getEmotionDrawable((EmojiProvider) mProvider, description, 0.6f);
            if (drawable != null) {
                ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                editable.setSpan(span, start, start + description.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            this.mAttachEditText.setSelection(editEnd);
        }
    }

    private void onGraphicItemClick(GraphicProvider provider, int page, int position) {
        final int index = page * Values.GRAPHIC_PER_PAGE + position;
        if (this.mEmotionSelectedListener != null) {
            GraphicItem item = provider.getValue(index);
            this.mEmotionSelectedListener.onGraphicSelected(provider.getEmotionCategory(),
                    item.getCategory(),
                    provider.getEmotionPath(item));
        }
    }

    public void setOnEmotionSelectedListener(@NonNull OnEmotionSelectedListener listener) {
        this.mEmotionSelectedListener = listener;
    }

    private boolean isGraphic() {
        String cate = mProvider.getEmotionCategory();
        return cate.equals(Values.CATEGORY_GRAPHIC_EMOTICONS);
    }
}
