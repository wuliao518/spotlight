# spotlight
模仿Gecco[https://github.com/yukiasai/Gecco]

![](/art/demo.gif)
```java
Button one = (Button) findViewById(R.id.one);
Button two = (Button) findViewById(R.id.two);
Button three = (Button) findViewById(R.id.three);

final SpotlightView spotlightView = new SpotlightView(getApplication());
spotlightView.addGuideView(MainActivity.this, one)
        .setShowView(R.mipmap.ic_launcher)
        .setDirection(SpotlightView.Direction.RIGHT)
        .setShape(SpotlightView.Shape.CIRCLE)
        .setLeftMargin(10)
        .setPadding(0)
        .build();

TextView text = new TextView(getApplication());
text.setText("我好想你啊！！");
text.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
        FrameLayout.LayoutParams.WRAP_CONTENT));

spotlightView.addGuideView(MainActivity.this, two)
        .setShowView(text)
        .setDirection(SpotlightView.Direction.RIGHT)
        .setLeftMargin(10)
        .setRadius(10)
        .setPadding(0)
        .build();

spotlightView.addGuideView(MainActivity.this, three)
        .setShowView(R.mipmap.ic_launcher)
        .setDirection(SpotlightView.Direction.RIGHT)
        .setLeftMargin(10)
        .setRadius(10)
        .setPadding(0)
        .build();
new Handler().postDelayed(new Runnable() {
    @Override
    public void run() {
        spotlightView.start();
    }
}, 500);
```
