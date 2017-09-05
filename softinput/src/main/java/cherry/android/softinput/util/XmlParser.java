package cherry.android.softinput.util;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import cherry.android.softinput.Values;
import cherry.android.softinput.model.EmojiItem;

/**
 * Created by ROOT on 2017/9/1.
 */

public final class XmlParser {
    private static final String TAG = "XmlParser";

    public static Map<String, EmojiItem> xmlParse(@Values.Category String cate, InputStream inputStream) throws Exception {
        Map<String, EmojiItem> map = null;
        EmojiItem emojiItem = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(inputStream, "utf-8");
        int event = parser.getEventType();
        String category = "";
        while (XmlPullParser.END_DOCUMENT != event) {
            final String name = parser.getName();
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    map = new LinkedHashMap<>();
                    break;
                case XmlPullParser.START_TAG:
                    if ("category".equals(name)) {
                        category = parser.getAttributeValue(0);
                    }
                    if ("item".equals(name)) {
                        emojiItem = parseEmoji(parser, cate, category);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("item".equals(name)) {
                        map.put(emojiItem.getDescription(), emojiItem);
                        emojiItem = null;
                    }
                    break;
                default:
                    break;
            }
            event = parser.next();
        }
        return map;
    }

    private static EmojiItem parseEmoji(XmlPullParser parser, @Values.Category String cate, String category) {
        String description = null;
        String id = null;
        String resource = null;
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String name = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);
            if (name.equals("description")) {
                description = value;
            } else if (name.equals("id")) {
                id = value;
            } else if (name.equals("resource")) {
                resource = cate + "/" + category + "/" + value;
            }
        }
        return new EmojiItem(id, resource, description, cate);
    }

    public static Map<String, EmojiItem> parseAssetsEmoji(Context context, @Values.Category String file, String assetsPath) {
        InputStream is = null;
        try {
            is = context.getAssets().open(assetsPath);
            return xmlParse(file, is);
        } catch (Exception e) {
            Log.e(TAG, "[parseAssetsEmoji]", e);
            e.printStackTrace();
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }
}
