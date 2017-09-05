package cherry.android.softinput;

/**
 * Created by ROOT on 2017/9/4.
 */

public interface OnEmotionSelectedListener {
    void onEmojiSelected(String category, String description);

    void onGraphicSelected(String category, String title, String path);
}
