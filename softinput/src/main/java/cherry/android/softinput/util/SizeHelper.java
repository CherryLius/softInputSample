package cherry.android.softinput.util;

import android.content.Context;

import cherry.android.softinput.Values;

/**
 * Created by ROOT on 2017/9/1.
 */

public final class SizeHelper {

    private SizeHelper() {
        throw new AssertionError("no instance.");
    }

    private static int _width;
    private static int _height;
    private static int _indicatorHeight;
    private static int _tabHeight;

    public static void resolveParentSize(int width, int height) {
        _width = width;
        _height = height;
    }

    public static void resolveTabAndIndicatorHeight(int tabHeight, int indicatorHeight) {
        _tabHeight = tabHeight;
        _indicatorHeight = indicatorHeight;
    }

    public static float resolveEmojiSize(Context context) {
        if (_width <= 0 || _height <= 0)
            return 0;
        int padding = (int) DensityUtils.dp2px(context, 25);
        int emojiTotalHeight = _height - _tabHeight - _indicatorHeight - padding * 2;
        float width = (float) _width / Values.EMOJI_COLUMNS;
        float height = (float) emojiTotalHeight / Values.EMOJI_ROWS;
        return Math.min(width, height);
    }

    public static float resolveGraphicSize(Context context) {
        if (_width <= 0 || _height <= 0)
            return 0;
        int padding = (int) DensityUtils.dp2px(context, 25);
        int graphicTotalHeight = _height - _tabHeight - _indicatorHeight - padding * 2;
        float width = (float) _width / Values.GRAPHIC_COLUMNS;
        float height = (float) graphicTotalHeight / Values.GRAPHIC_ROWS;
        return Math.min(width, height);
    }
}
