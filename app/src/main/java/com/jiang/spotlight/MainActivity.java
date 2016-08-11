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
        TextView text = new TextView(getApplication());
        text.setText("Appear spotlight on view");
        text.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        spotlightView.addGuideView(MainActivity.this, imageShare)
                .setShowView(text)
                .setDirection(SpotlightView.Direction.BOTTOM)
                .setShape(SpotlightView.Shape.CIRCLE)
                .setLeftMargin(-240)
                .setTopMargin(24)
                .setPadding(18)
                .build(0);


        spotlightView.addGuideView(MainActivity.this, imageAdd)
                .setShowView(text)
                .setDirection(SpotlightView.Direction.BOTTOM)
                .setShape(SpotlightView.Shape.CIRCLE)
                .setLeftMargin(-240)
                .setTopMargin(24)
                .setPadding(18)
                .build(1);


        spotlightView.addGuideView(MainActivity.this, title)
                .setShowView(text)
                .setDirection(SpotlightView.Direction.BOTTOM)
                .setShape(SpotlightView.Shape.RECT)
                .setTopMargin(24)
                .setLeftMargin(-40)
                .setPadding(18)
                .setRadius(10)
                .build(2);


        TextView gecco = new TextView(getApplication());
        gecco.setText("Gecco");
        gecco.setTextSize(48);
        gecco.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        spotlightView.addGuideView(MainActivity.this, findViewById(android.R.id.content))
                .setShowView(gecco)
                .setDirection(SpotlightView.Direction.CENTER)
                .setShape(SpotlightView.Shape.CIRCLE)
                .setPadding(-400)
                .setRadius(10)
                .build(3);

//        Button one = (Button) findViewById(R.id.one);
//        Button two = (Button) findViewById(R.id.two);
//        Button three = (Button) findViewById(R.id.three);


        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                spotlightView.start();
            }
        });


//        spotlightView.addGuideView(MainActivity.this, one)
//                .setShowView(R.mipmap.ic_launcher)
//                .setDirection(SpotlightView.Direction.RIGHT)
//                .setShape(SpotlightView.Shape.CIRCLE)
//                .setLeftMargin(10)
//                .setPadding(0)
//                .build();
//
//        TextView text = new TextView(getApplication());
//        text.setText("我好想你啊！！");
//        text.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT));
//
//        spotlightView.addGuideView(MainActivity.this, two)
//                .setShowView(text)
//                .setDirection(SpotlightView.Direction.RIGHT)
//                .setLeftMargin(10)
//                .setRadius(10)
//                .setPadding(0)
//                .build();
//
//        spotlightView.addGuideView(MainActivity.this, three)
//                .setShowView(R.mipmap.ic_launcher)
//                .setDirection(SpotlightView.Direction.RIGHT)
//                .setLeftMargin(10)
//                .setRadius(10)
//                .setPadding(0)
//                .build();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                spotlightView.prepare();
                spotlightView.start();
            }
        }, 500);


    }
}
