package cherry.android.softinput.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cherry.android.softinput.R;
import cherry.android.softinput.Values;
import cherry.android.softinput.emoji.EmojiProvider;
import cherry.android.softinput.model.EmojiItem;
import cherry.android.softinput.util.SizeHelper;

/**
 * Created by ROOT on 2017/9/1.
 */

public class EmojiAdapter extends BaseAdapter {

    private Context mContext;
    private EmojiProvider mProvider;
    private int mStart;

    public EmojiAdapter(@NonNull Context context, EmojiProvider provider, int start) {
        this.mContext = context;
        this.mProvider = provider;
        this.mStart = start;
    }

    @Override
    public int getCount() {
        final int count = mProvider.getCount() - mStart + 1;
        return Math.min(count, Values.EMOJI_PER_PAGE + 1);
    }

    @Override
    public Object getItem(int position) {
        return mStart + position;
    }

    @Override
    public long getItemId(int position) {
        return mStart + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ImageView icon;
        if (convertView == null) {
            RelativeLayout rl = new RelativeLayout(mContext);
            rl.setGravity(Gravity.CENTER);
            final float size = SizeHelper.resolveEmojiSize(mContext);
            rl.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int) size));

            icon = new ImageView(mContext);

            int iconSize = (int) (size * 0.7f);
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
        if (position == Values.EMOJI_PER_PAGE || (mStart + position == mProvider.getCount())) {
            icon.setImageResource(R.drawable.ic_emoji_del);
        } else {
            EmojiItem item = mProvider.getValue(mStart + position);
            icon.setImageDrawable(mProvider.getEmotion(item));
        }

        return convertView;
    }
}
