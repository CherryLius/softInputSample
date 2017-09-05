package cherry.android.softinput.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cherry.android.softinput.EmotionKit;
import cherry.android.softinput.emoji.EmojiProvider;

/**
 * Created by LHEE on 2017/9/2.
 */

public final class EmotionUtils {
    private EmotionUtils() {
        throw new AssertionError("no instance.");
    }

    public static void replaceEmotions(Editable editable, int start, int count) {
        if (count < 0 || editable.length() < (start + count))
            return;
        CharSequence charSequence = editable.subSequence(start, start + count);
        Pattern pattern = Pattern.compile("\\[([\u4e00-\u9fa5\\w])+?\\]");
        Matcher matcher = pattern.matcher(charSequence);

        while (matcher.find()) {
            int from = start + matcher.start();
            int to = start + matcher.end();
            String emotion = editable.subSequence(from, to).toString();
            Log.i("Test", charSequence + ", emotion=" + emotion);

//            Drawable drawable = getEmotionDrawable(context, emotion, 0.6f);
//            if (drawable != null) {
//                ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
//                editable.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
        }
    }

    public static Drawable getEmotionDrawable(EmojiProvider provider, String description, float scale) {
        Drawable drawable = provider.getEmotion(description);
        if (drawable != null) {
//            final int width = (int) (drawable.getIntrinsicWidth() * scale);
//            final int height = (int) (drawable.getIntrinsicHeight() * scale);
            final float size = SizeHelper.resolveEmojiSize(EmotionKit.get().getContext());
            final int width = (int) (size * scale);
            final int height = (int) (size * scale);
            drawable.setBounds(0, 0, width, height);
        }
        return drawable;
    }
}
