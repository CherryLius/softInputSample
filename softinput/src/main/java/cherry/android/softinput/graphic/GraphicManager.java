package cherry.android.softinput.graphic;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import cherry.android.softinput.EmotionKit;
import cherry.android.softinput.EmotionProvider;
import cherry.android.softinput.model.GraphicCategory;

/**
 * Created by ROOT on 2017/9/4.
 */

public final class GraphicManager {
    private static final String TAG = "GraphicManager";
    public static final String GRAPHIC_ASSETS = "graphic";
    private AtomicBoolean atomicBoolean;

    private static GraphicManager _instance;
    private static ReentrantLock _lock = new ReentrantLock();
    private Map<String, GraphicProvider> mCategoryMap = new HashMap<>();
    private List<GraphicProvider> mCategoryList;
    private AssetsCopyListener mListener;

    public List<? extends EmotionProvider> getProvider() {
        return this.mCategoryList;
    }

    public boolean isAssetCopyComplete() {
        return atomicBoolean.get();
    }

    public void setAssetsCopyListener(@NonNull AssetsCopyListener listener) {
        this.mListener = listener;
    }

    private GraphicManager() {
        atomicBoolean = new AtomicBoolean(false);
    }

    public static GraphicManager get() {
        if (_instance != null)
            return _instance;
        _lock.lock();
        if (_instance == null)
            _instance = new GraphicManager();
        _lock.unlock();
        return _instance;
    }

    public void startCopyAssets(@NonNull final Context context, @NonNull final String assetPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                atomicBoolean.set(false);
                copyAssetGraphicPath(context, assetPath);
                loadGraphicEmoticons();
                atomicBoolean.set(true);
                if (mListener != null) {
                    mListener.onComplete();
                }
            }
        }).start();
    }

    private static void copyAssetGraphicPath(@NonNull Context context, @NonNull String assetPath) {
        File cacheFile = EmotionKit.get().getGraphicPath().getParentFile();
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        String filePath = cacheFile.getAbsolutePath();

        AssetManager assetManager = context.getAssets();
        List<String> srcFile = new ArrayList<>();
        try {
            String[] paths = assetManager.list(assetPath);
            for (int i = 0; i < paths.length; i++) {
                String fileName = assetPath + File.separator + paths[i];
                File file = new File(filePath, fileName);
                if (!file.exists()) {
                    srcFile.add(fileName);
                }
            }
            if (srcFile.size() > 0) {
                copyGraphicFromAssets(context, filePath, srcFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "error", e);
        }
    }

    private static void copyGraphicFromAssets(@NonNull Context context,
                                              final String savePath,
                                              final List<String> srcFile) throws IOException {
        AssetManager assetManager = context.getAssets();
        for (int i = 0; i < srcFile.size(); i++) {
            String assetSrc = srcFile.get(i);
            String[] fileNames = assetManager.list(assetSrc);
            if (fileNames.length > 0) {
                copyAssetGraphicPath(context, assetSrc);
            } else {
                InputStream is = assetManager.open(assetSrc);
                File file = new File(savePath, assetSrc);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, length);
                }
                fos.flush();
                fos.close();
                is.close();
            }
        }
    }

    private void loadGraphicEmoticons() {
        File file = EmotionKit.get().getGraphicPath();
        if (!file.exists())
            return;
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isDirectory()) {
                String name = f.getName();
                GraphicCategory cate = new GraphicCategory(name, name, i);
                mCategoryMap.put(name, new GraphicProvider(cate));
            }
        }
        mCategoryList = new ArrayList<>(mCategoryMap.values());
        Collections.sort(mCategoryList, new Comparator<GraphicProvider>() {
            @Override
            public int compare(GraphicProvider t0, GraphicProvider t1) {
                return t0.getCategory().getOrder() - t1.getCategory().getOrder();
            }
        });
    }

    public interface AssetsCopyListener {
        void onComplete();
    }
}
