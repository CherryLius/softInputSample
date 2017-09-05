package cherry.android.softinput.adapter;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cherry.android.softinput.EmotionKit;
import cherry.android.softinput.Values;
import cherry.android.softinput.graphic.GraphicProvider;
import cherry.android.softinput.model.GraphicItem;
import cherry.android.softinput.util.SizeHelper;

/**
 * Created by ROOT on 2017/9/4.
 */

public class GraphicAdapter extends BaseAdapter {

    private Context mContext;
    private GraphicProvider mProvider;
    private final int mStart;

    public GraphicAdapter(Context context, GraphicProvider provider, int start) {
        this.mContext = context;
        this.mProvider = provider;
        this.mStart = start;
    }

    @Override
    public int getCount() {
        final int count = mProvider.getCount() - mStart;
        return Math.min(count, Values.GRAPHIC_PER_PAGE);
    }

    @Override
    public Object getItem(int i) {
        return mProvider.getValue(mStart + i);
    }

    @Override
    public long getItemId(int i) {
        return mStart + i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ImageView icon;
        if (convertView == null) {
            RelativeLayout rl = new RelativeLayout(mContext);
            rl.setGravity(Gravity.CENTER);
            final float size = SizeHelper.resolveGraphicSize(mContext);
            rl.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int) size));

            icon = new ImageView(mContext);
            int iconSize = (int) (size * 0.8f);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(iconSize,
                    iconSize);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            icon.setLayoutParams(params);

            rl.addView(icon);
            convertView = rl;
            convertView.setTag(icon);
        } else {
            icon = (ImageView) convertView.getTag();
        }
        if (position == Values.GRAPHIC_PER_PAGE || (mStart + position == mProvider.getCount())) {
            return convertView;
        }
        GraphicItem item = mProvider.getValue(mStart + position);
        EmotionKit.get().getImageLoader().showImage(icon, mProvider.getEmotion(item));
        return convertView;
    }
}
