package cherry.android.softinput;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by ROOT on 2017/9/4.
 */

public interface EmotionProvider<T, R, I> {

    @NonNull
    R getEmotion(@NonNull T value);

    @Nullable
    T getValue(@NonNull int position);

    int getCount();

    @Values.Category
    String getEmotionCategory();

    I getIndicator();
}
