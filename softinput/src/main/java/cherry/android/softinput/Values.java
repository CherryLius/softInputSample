package cherry.android.softinput;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ROOT on 2017/9/4.
 */

public interface Values {

    String KEY_DEL = "/DEL";

    String CATEGORY_FACIAL = "facial";
    String CATEGORY_EMOJI = "emoji";
    String CATEGORY_GRAPHIC_EMOTICONS = "graphic_emoticons";

    @StringDef({CATEGORY_EMOJI, CATEGORY_FACIAL, CATEGORY_GRAPHIC_EMOTICONS})
    @Retention(RetentionPolicy.SOURCE)
    @interface Category {
    }

    int EMOJI_COLUMNS = 7;
    int EMOJI_ROWS = 3;
    int EMOJI_PER_PAGE = EMOJI_COLUMNS * EMOJI_ROWS - 1;

    int GRAPHIC_COLUMNS = 4;
    int GRAPHIC_ROWS = 2;
    int GRAPHIC_PER_PAGE = GRAPHIC_COLUMNS * GRAPHIC_ROWS;
}
