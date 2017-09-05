package cherry.android.softinput;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

import static cherry.android.softinput.graphic.GraphicManager.GRAPHIC_ASSETS;
import static cherry.android.softinput.graphic.GraphicManager.copyAssetGraphicPath;

/**
 * Created by ROOT on 2017/9/1.
 */

public final class EmotionKit {
    private static EmotionKit _instance;
    private static ReentrantLock _lock = new ReentrantLock();

    private EmotionKit() {

    }

    public static EmotionKit get() {
        if (_instance != null)
            return _instance;
        _lock.lock();
        if (_instance == null)
            _instance = new EmotionKit();
        _lock.unlock();
        return _instance;
    }

    private Context mContext;
    private ImageLoader mImageLoader;

    public void init(@NonNull Context context) {
        mContext = context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                copyAssetGraphicPath(mContext, GRAPHIC_ASSETS);
            }
        }).start();
    }

    public void setImageLoader(@NonNull ImageLoader imageLoader) {
        this.mImageLoader = imageLoader;
    }

    @NonNull
    public ImageLoader getImageLoader() {
        return this.mImageLoader;
    }

    @NonNull
    public Context getContext() {
        return this.mContext;
    }

    public File getGraphicPath() {
        return new File(mContext.getExternalCacheDir(), GRAPHIC_ASSETS);
    }
}
