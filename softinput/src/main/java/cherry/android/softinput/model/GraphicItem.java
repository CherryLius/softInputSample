package cherry.android.softinput.model;

/**
 * Created by ROOT on 2017/9/4.
 */

public class GraphicItem {
    private String category;
    private String filename;

    public GraphicItem(String category, String filename) {
        this.category = category;
        this.filename = filename;
    }

    public String getCategory() {
        return category;
    }

    public String getFilename() {
        return filename;
    }
}
