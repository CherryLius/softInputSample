package cherry.android.softinput.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cherry.android.softinput.EmotionKit;
import cherry.android.softinput.R;

/**
 * Created by ROOT on 2017/9/1.
 */

public class EmotionTab extends RelativeLayout {

    private ImageView mIcon;

    public EmotionTab(Context context, @NonNull Object object) {
        super(context);
        initView();
        if (object == null) {
            mIcon.setImageResource(R.drawable.ic_tab_emoji);
        } else if (object instanceof Integer) {
            int iconId = (int) object;
            mIcon.setImageResource(iconId);
        } else if (object instanceof String) {
            EmotionKit.get().getImageLoader().showImage(mIcon, (String) object);
        } else if (object instanceof Drawable) {
            Drawable drawable = (Drawable) object;
            mIcon.setImageDrawable(drawable);
        } else if (object instanceof Bitmap) {
            Bitmap bitmap = (Bitmap) object;
            mIcon.setImageBitmap(bitmap);
        }
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_emotion_tab, this);
        mIcon = (ImageView) findViewById(R.id.icon);
        setBackgroundResource(R.drawable.ic_tab_state_bg);
    }
}
