package cherry.android.softinput.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cherry.android.softinput.EmotionKit;
import cherry.android.softinput.ImageLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmotionKit.get().init(getApplicationContext());
        EmotionKit.get().setImageLoader(new ImageLoader() {
            @Override
            public void showImage(@NonNull View view, @NonNull String url) {
                Glide.with(view.getContext()).load(url).into((ImageView) view);
            }
        });
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EmotionActivity.class));
            }
        });
    }
}
