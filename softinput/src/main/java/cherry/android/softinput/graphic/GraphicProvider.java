package cherry.android.softinput.graphic;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.List;

import cherry.android.softinput.EmotionKit;
import cherry.android.softinput.EmotionProvider;
import cherry.android.softinput.Values;
import cherry.android.softinput.model.GraphicCategory;
import cherry.android.softinput.model.GraphicItem;

/**
 * Created by ROOT on 2017/9/4.
 */

public class GraphicProvider implements EmotionProvider<GraphicItem, String, String> {

    private GraphicCategory mCategory;

    public GraphicProvider(GraphicCategory category) {
        this.mCategory = category;
    }

    public GraphicCategory getCategory() {
        return this.mCategory;
    }

    @NonNull
    @Override
    public String getEmotion(@NonNull GraphicItem value) {
        return "file://" + getEmotionPath(value);
    }

    public String getEmotionPath(GraphicItem value) {
        File file = EmotionKit.get().getGraphicPath();
        return new File(file, value.getCategory() + File.separator + value.getFilename()).getAbsolutePath();
    }

    @Nullable
    @Override
    public GraphicItem getValue(@NonNull int position) {
        return mCategory.getItems().get(position);
    }

    @Override
    public int getCount() {
        List<GraphicItem> items = mCategory.getItems();
        return items == null ? 0 : items.size();
    }

    @Override
    public String getEmotionCategory() {
        return Values.CATEGORY_GRAPHIC_EMOTICONS;
    }

    @Override
    public String getIndicator() {
        return mCategory.getTabUrl();
    }
}
