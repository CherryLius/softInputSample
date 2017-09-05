package cherry.android.softinput.model;

/**
 * Created by ROOT on 2017/9/1.
 */

public class EmojiItem {
    private String id;
    private String resource;
    private String description;
    private String category;

    public EmojiItem(String id, String resource, String description, String category) {
        this.id = id;
        this.resource = resource;
        this.description = description;
        this.category = category;
    }

    public EmojiItem(String id, String resource, String description) {
        this.id = id;
        this.resource = resource;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getResource() {
        return resource;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }
}
