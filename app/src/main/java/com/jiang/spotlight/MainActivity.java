package com.jiang.spotlight;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiang.library.widget.SpotlightView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView title = (TextView) findViewById(R.id.title);
        final ImageView imageAdd = (ImageView) findViewById(R.id.image_add);
        final ImageView imageShare = (ImageView) findViewById(R.id.image_share);

        final SpotlightView spotlightView = new SpotlightView(getApplication());
        TextView oneText = new TextView(getApplication());
        oneText.setText("This Icon For Share");
        oneText.setTextSize(16);
        oneText.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        spotlightView.addGuideView(MainActivity.this, imageShare)
                .setShowView(oneText)
                .setDirection(SpotlightView.BOTTOM | SpotlightView.LEFT)
                .setShape(SpotlightView.Shape.CIRCLE)
                .setLeftMargin(-60)
                .setTopMargin(24)
                .setPadding(16)
                .build(0);

        TextView twoText = new TextView(getApplication());
        twoText.setText("This Icon For Add");
        twoText.setTextSize(16);
        twoText.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        spotlightView.addGuideView(MainActivity.this, imageAdd)
                .setShowView(twoText)
                .setDirection(SpotlightView.BOTTOM | SpotlightView.CENTER_HORIZONTAL)
                .setShape(SpotlightView.Shape.CIRCLE)
                .setLeftMargin(-40)
                .setTopMargin(24)
                .setPadding(16)
                .build(1);

        TextView threeText = new TextView(getApplication());
        threeText.setText("This Is Title");
        threeText.setTextSize(16);
        threeText.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        spotlightView.addGuideView(MainActivity.this, title)
                .setShowView(threeText)
                .setDirection(SpotlightView.BOTTOM | SpotlightView.CENTER_HORIZONTAL)
                .setShape(SpotlightView.Shape.RECT)
                .setTopMargin(24)
                .setPadding(18)
                .setRadius(10)
                .build(2);


        TextView fourText = new TextView(getApplication());
        fourText.setText("That Is All");
        fourText.setTextSize(42);
        fourText.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        spotlightView.addGuideView(MainActivity.this, findViewById(R.id.example))
                .setShowView(fourText)
                .setDirection(SpotlightView.PARENT_CENTER)
                .setShape(SpotlightView.Shape.CIRCLE)
                .setPadding(-1 * DisplayUtil.getInstance().dp2px(getApplication(), 180))
                .setRadius(10)
                .build(3);


        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spotlightView.start();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                spotlightView.prepare();
                spotlightView.start();
            }
        }, 500);


    }
}
