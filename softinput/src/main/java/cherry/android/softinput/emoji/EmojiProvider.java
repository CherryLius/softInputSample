package cherry.android.softinput.emoji;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LruCache;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cherry.android.softinput.EmotionProvider;
import cherry.android.softinput.R;
import cherry.android.softinput.Values;
import cherry.android.softinput.model.EmojiItem;
import cherry.android.softinput.util.XmlParser;

/**
 * Created by ROOT on 2017/9/4.
 */

public class EmojiProvider implements EmotionProvider<EmojiItem, Drawable, Integer> {
    private static final int MAX_MEMORY_SIZE = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private static final LruCache<String, Bitmap> MEMORY_CACHE = new LruCache<String, Bitmap>(MAX_MEMORY_SIZE) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight() / 1024;
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            super.entryRemoved(evicted, key, oldValue, newValue);
            if (oldValue != newValue) {
                oldValue.recycle();
            }
        }
    };

    private Context mContext;
    private Map<String, EmojiItem> mEmojiMap;
    private List<EmojiItem> mEmojiList;
    @Values.Category
    private String mCategory;

    public EmojiProvider(@NonNull Context context, @Values.Category String category, @NonNull String assetsPath) {
        mContext = context;
        this.mCategory = category;
        mEmojiMap = XmlParser.parseAssetsEmoji(context, category, assetsPath);
        final int emojiSize = mEmojiMap.size();
        if (emojiSize == 0)
            return;
        mEmojiList = new ArrayList<>(mEmojiMap.values());
        final int page = (int) Math.ceil(emojiSize / (float) Values.EMOJI_PER_PAGE);
        final int whole = page * Values.EMOJI_PER_PAGE;
        final int delta = whole - emojiSize;
        for (int i = 0; i < delta; i++) {
            mEmojiList.add(new EmojiItem("", "", ""));
        }
    }

    @Nullable
    @Override
    public Drawable getEmotion(@NonNull EmojiItem emojiItem) {
        if (TextUtils.isEmpty(emojiItem.getDescription()))
            return null;
        Bitmap bitmap = MEMORY_CACHE.get(emojiItem.getResource());
        if (bitmap == null) {
            bitmap = loadAssetsEmoji(this.mContext, emojiItem.getResource());
            MEMORY_CACHE.put(emojiItem.getResource(), bitmap);
        }
        return new BitmapDrawable(this.mContext.getResources(), bitmap);
    }

    @Nullable
    @Override
    public EmojiItem getValue(@NonNull int position) {
        if (position < 0 || position > mEmojiList.size() - 1)
            return null;
        return mEmojiList.get(position);
    }

    public Drawable getEmotion(@NonNull String description) {
        return getEmotion(mEmojiMap.get(description));
    }

    @Override
    public int getCount() {
        return mEmojiList != null ? mEmojiList.size() : 0;
    }

    @Override
    public String getEmotionCategory() {
        return this.mCategory;
    }

    @Override
    public Integer getIndicator() {
        if (this.mCategory.equals(Values.CATEGORY_FACIAL)) {
            return R.drawable.ic_tab_emoji;
        } else if (this.mCategory.equals(Values.CATEGORY_EMOJI)) {
            return R.drawable.ic_tab_emoji_3;
        }
        throw new IllegalArgumentException("category not support. " + this.mCategory);
    }

    private static Bitmap loadAssetsEmoji(@NonNull Context context, @NonNull String assetsPath) {
        InputStream is = null;
        try {
            Resources resources = context.getResources();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDensity = DisplayMetrics.DENSITY_HIGH;
            options.inScreenDensity = resources.getDisplayMetrics().densityDpi;
            options.inTargetDensity = resources.getDisplayMetrics().densityDpi;
            is = context.getAssets().open(assetsPath);
            Bitmap bitmap = BitmapFactory.decodeStream(is, new Rect(), options);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
