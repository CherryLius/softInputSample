package cherry.android.softinput.emoji;

import java.util.HashMap;
import java.util.Map;

import cherry.android.softinput.EmotionKit;
import cherry.android.softinput.Values;

/**
 * Created by ROOT on 2017/9/4.
 */

public class EmojiManager {
    private static final Map<String, EmojiProvider> EMOJI_PROVIDER_MAP = new HashMap<>();

    public static EmojiProvider getEmoji(@Values.Category String category) {
        EmojiProvider provider = EMOJI_PROVIDER_MAP.get(category);
        if (provider == null) {
            provider = new EmojiProvider(EmotionKit.get().getContext(), category, category + "/emoji.xml");
            EMOJI_PROVIDER_MAP.put(category, provider);
        }
        return provider;
    }
}
