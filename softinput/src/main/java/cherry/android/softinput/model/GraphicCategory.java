package cherry.android.softinput.model;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import cherry.android.softinput.EmotionKit;
import cherry.android.softinput.Values;

/**
 * Created by ROOT on 2017/9/4.
 */

public class GraphicCategory {
    private String name;
    private String title;
    private int order;
    private List<GraphicItem> items;

    public GraphicCategory(String name, String title, int order) {
        this.name = name;
        this.title = title;
        this.order = order;
        this.items = loadItems(name);
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public int getOrder() {
        return order;
    }

    public List<GraphicItem> getItems() {
        return items;
    }

    public String getTabUrl() {
        File[] files = EmotionKit.get().getGraphicPath().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().startsWith(name);
            }
        });
        if (files.length == 0)
            return null;
        File file = files[0];
        return "file://" + file.getAbsolutePath();
    }

    private static List<GraphicItem> loadItems(String path) {
        File file = new File(EmotionKit.get().getGraphicPath(), path);
        if (!file.exists())
            return null;
        List<GraphicItem> list = new ArrayList<>();
        File[] files = file.listFiles();
        for (File f : files) {
            list.add(new GraphicItem(path, f.getName()));
        }

        final int size = list.size();
        final int page = (int) Math.ceil(size / (float) Values.GRAPHIC_PER_PAGE);
        final int whole = page * Values.GRAPHIC_PER_PAGE;
        final int delta = whole - size;
        for (int i = 0; i < delta; i++) {
            list.add(new GraphicItem("", ""));
        }
        return list;
    }
}
