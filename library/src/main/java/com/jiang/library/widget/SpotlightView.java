package com.jiang.library.widget;

/**
 * Created by wuliao on 16/7/13.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jiang.library.model.Spotlight;
import com.jiang.library.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/12 0012.
 */

public class SpotlightView extends FrameLayout {
    private boolean isAnimation;

    private RectF currentRectF = new RectF(0, 0, 200, 200);
    private float currentRadius;
    private Paint mPaint;
    private Bitmap mMaskBitmap;

    public SpotlightView(Context context) {
        this(context, null);
    }

    public SpotlightView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpotlightView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private List<Spotlight> spotlights;

    private Builder builder;
    private Activity activity;
    private int mDurationTime = 300;

    public Builder addGuideView(Activity activity, final View targetView) {
        this.activity = activity;
        if (targetView == null) {
            throw new IllegalArgumentException("targetView can not be null");
        }
        builder = new Builder(targetView);
        return builder;
    }

    public static int TOP = 0x0001;//在targetView之上
    public static int LEFT = 0x0002;//在targetView之左
    public static int RIGHT = 0x0004;//在targetView之右
    public static int BOTTOM = 0x0008;//在targetView之下
    public static final int CENTER_VERTICAL = 0x0010;//在targetView竖直居中
    public static final int CENTER_HORIZONTAL = 0x0020;//在targetView水平居中


    public static int PARENT_TOP = 0x0040;
    public static int PARENT_LEFT = 0x0080;
    public static int PARENT_RIGHT = 0x0100;
    public static int PARENT_BOTTOM = 0x0200;
    public static int PARENT_CENTER = CENTER_VERTICAL | CENTER_HORIZONTAL;

    public enum Shape {
        CIRCLE,
        RECT
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        spotlights = new ArrayList<>();
    }

    private int maskColor = 0x77000000;
    private static final PorterDuffXfermode MODE_DST_OUT = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (currentRectF != null) {
            mMaskBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas myCanvas = new Canvas(mMaskBitmap);
            myCanvas.drawColor(maskColor);
            mPaint.setXfermode(MODE_DST_OUT);
            myCanvas.drawRoundRect(currentRectF, currentRadius, currentRadius, mPaint);
            canvas.drawBitmap(mMaskBitmap, 0, 0, new Paint());
        }
        super.dispatchDraw(canvas);
    }


    public void prepare() {

        int size = spotlights.size();
        for (int i = 0; i < size; i++) {
            final Spotlight model = spotlights.get(i);
            if (model.getShowAnimator() == null) {//没有开始动画,一般是第一个
                ValueAnimator showAnimator = getShowAnimator(model);
                currentRadius = model.getRadius();
                model.setShowAnimator(showAnimator);
            }
            if (i + 1 < size) {//不是最后一个
                final Spotlight nextModel = spotlights.get(i + 1);
                if (nextModel.getRadius() == model.getRadius()) {//直接移动过去然后进行变换
                    ValueAnimator showAnimator = ValueAnimator.ofObject(new TypeEvaluator<RectF>() {
                        @Override
                        public RectF evaluate(float fraction, RectF start, RectF end) {
                            float startLeft = start.left;
                            float endLeft = end.left;
                            float startTop = start.top;
                            float endTop = end.top;

                            float startRight = start.right;
                            float endRight = end.right;
                            float startBottom = start.bottom;
                            float endBottom = end.bottom;

                            float left = startLeft + fraction * (endLeft - startLeft);
                            float top = startTop + fraction * (endTop - startTop);
                            float right = startRight + fraction * (endRight - startRight);
                            float bottom = startBottom + fraction * (endBottom - startBottom);
                            return new RectF(left, top, right, bottom);
                        }
                    }, model.getRectF(), nextModel.getRectF());
                    showAnimator.setDuration(mDurationTime);
                    showAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            RectF tempRect = (RectF) valueAnimator.getAnimatedValue();
                            currentRectF = tempRect;
                            invalidate();
                        }
                    });
                    nextModel.setShowAnimator(showAnimator);
                    model.setHideAnimator(null);
                } else {
                    model.setHideAnimator(getHideAnimator(model));
                    nextModel.setShowAnimator(getShowAnimator(nextModel));
                }
                model.setListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animator begin = nextModel.getShowAnimator();
                        begin.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                removeView(model.getShowView());
                                if (nextModel.getShowView().getParent() == null) {
                                    addView(nextModel.getShowView(), nextModel.getParams());
                                }
                                setOnClickListener(nextModel.getListener());
                            }
                        });
                        Animator end = model.getHideAnimator();
                        if (end != null) {
                            AnimatorSet animSet = new AnimatorSet();
                            animSet.play(end).before(begin);
                            animSet.start();
                        } else {
                            begin.start();
                        }
                    }
                });
            } else {//last one
                model.setHideAnimator(getHideAnimator(model));
                model.setListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animator animator = model.getHideAnimator();
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                removeAllViews();
                                clearGuide();
                            }
                        });
                        animator.start();
                    }
                });
            }
        }


    }

    public void start() {
        ViewGroup.LayoutParams guideParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        final ViewGroup contentView = (ViewGroup) activity.getWindow().getDecorView();

        if (getParent() == null) {
            contentView.addView(SpotlightView.this, guideParams);
        }


        final Spotlight model = spotlights.get(0);
        Animator begin = model.getShowAnimator();
        setOnClickListener(model.getListener());
        begin.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (model.getShowView().getParent() == null) {
                    addView(model.getShowView(), model.getParams());
                }
            }
        });

//        Animator end = null;
//        if (position > 0) {
//            end = spotlights.get(position - 1).getHideAnimator();
//        }
//        if (end != null) {
//            AnimatorSet animSet = new AnimatorSet();
//            animSet.play(end).before(begin);
//            animSet.start();
//        } else {
            begin.start();
//        }


    }

    public void clearGuide() {
        if (activity != null) {
            final ViewGroup contentView = (ViewGroup) activity.getWindow().getDecorView();
            contentView.removeView(SpotlightView.this);
        }
    }


    public class Builder {

        int direction = BOTTOM | CENTER_HORIZONTAL;

        private Spotlight spotlight;
        private View targetView;
        private View showView;
        private int leftMargin, topMargin;
        private int radius = 5;
        private float scale = 1.0f;
        private float padding = 6;
        private Shape shape = Shape.RECT;

        public void build(final int position) {
            targetView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int targetWidth = targetView.getMeasuredWidth();
                    int targetHeight = targetView.getMeasuredHeight();
                    int[] location = new int[2];
                    targetView.getLocationOnScreen(location);

                    int width = DisplayUtil.getInstance().getViewMeasuredWidth(showView);
                    int height = DisplayUtil.getInstance().getViewMeasuredHeight(showView);

                    LayoutParams params = new LayoutParams(width, height);

                    while (direction > 0) {
                        if ((direction & TOP) > 0) {
                            params.topMargin = location[1] - height - topMargin;
                            direction = direction ^ TOP;
                        } else if ((direction & LEFT) > 0) {
                            params.leftMargin = location[0] - width - leftMargin;
                            direction = direction ^ LEFT;
                        } else if ((direction & RIGHT) > 0) {
                            params.leftMargin = location[0] + targetWidth + leftMargin;
                            direction = direction ^ RIGHT;
                        } else if ((direction & BOTTOM) > 0) {
                            params.topMargin = location[1] + targetHeight + topMargin;
                            direction = direction ^ BOTTOM;
                        } else if ((direction & CENTER_HORIZONTAL) > 0) {
                            params.leftMargin = location[0] + (targetWidth - width) / 2 + leftMargin;
                            direction = direction ^ CENTER_HORIZONTAL;
                        } else if ((direction & CENTER_VERTICAL) > 0) {
                            params.topMargin = location[1] + (targetHeight - height) / 2 + topMargin;
                            direction = direction ^ CENTER_VERTICAL;
                        } else if ((direction & PARENT_TOP) > 0) {
                            params.topMargin = location[1] + topMargin;
                            direction = direction ^ PARENT_TOP;
                        } else if ((direction & PARENT_LEFT) > 0) {
                            params.leftMargin = location[0] + leftMargin;
                            direction = direction ^ PARENT_LEFT;
                        } else if ((direction & PARENT_RIGHT) > 0) {
                            params.leftMargin = location[0] + targetWidth - width - leftMargin;
                            direction = direction ^ PARENT_RIGHT;
                        } else if ((direction & PARENT_BOTTOM) > 0) {
                            params.topMargin = location[1] + targetHeight - height + topMargin;
                            direction = direction ^ PARENT_BOTTOM;
                        }
                    }

                    RectF rectF;
                    if (shape == Shape.CIRCLE) {
                        float diameter = Math.max(targetWidth, targetHeight) + 2 * padding;
                        float x = location[0] + targetWidth / 2.0f;
                        float y = location[1] + targetHeight / 2.0f;
                        radius = (int) (diameter / 2);
                        rectF = new RectF(x - diameter / 2, y - diameter / 2, x + diameter / 2, y + diameter / 2);
                    } else {
                        rectF = new RectF(location[0] - padding, location[1] - padding,
                                location[0] + targetWidth + padding, location[1] + targetHeight + padding);
                    }
                    Spotlight light = new Spotlight(rectF, radius);
                    light.setParams(params);
                    light.setShowView(showView);
                    if (position > spotlights.size()) {
                        spotlights.add(light);
                    } else {
                        spotlights.add(position, light);
                    }

                    targetView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
            builder = null;
        }

        public Builder(View targetView) {
            this.targetView = targetView;
        }

        public Spotlight getSpotlight() {
            return spotlight;
        }

        public void setSpotlight(Spotlight spotlight) {
            this.spotlight = spotlight;
        }

        public Builder setDirection(int direction) {
            this.direction = direction;
            return this;
        }

        public Builder setShowView(View showView) {
            this.showView = showView;
            return this;
        }

        public Builder setShowView(int resId) {
            ImageView image = new ImageView(getContext());
            image.setImageResource(resId);
            this.showView = image;
            return this;
        }

        public Builder setLeftMargin(int leftMargin) {
            this.leftMargin = leftMargin;
            return this;
        }

        public Builder setTopMargin(int topMargin) {
            this.topMargin = topMargin;
            return this;
        }

        public Builder setShape(Shape shape) {
            this.shape = shape;
            return this;
        }

        public Builder setPadding(int padding) {
            this.padding = padding;
            return this;
        }

        public Builder setRadius(int radius) {
            this.radius = radius;
            return this;
        }

    }


    private ValueAnimator getShowAnimator(final Spotlight model) {
        ValueAnimator showAnimator = ValueAnimator.ofFloat(0.5f, 1.0f);
        showAnimator.setDuration(mDurationTime);
        showAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                RectF tempRect = model.getRectF();
                float value = (float) valueAnimator.getAnimatedValue();
                float width = tempRect.width() * (1.0f - value) / 2;
                float height = tempRect.height() * (1.0f - value) / 2;
                currentRectF = new RectF(tempRect.left + width, tempRect.top + height, tempRect.right - width, tempRect.bottom - height);
                currentRadius = model.getRadius();
                invalidate();
            }
        });
        return showAnimator;
    }


    private ValueAnimator getHideAnimator(final Spotlight model) {
        ValueAnimator showAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
        showAnimator.setDuration(mDurationTime);
        showAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                RectF tempRect = model.getRectF();
                float value = (float) valueAnimator.getAnimatedValue();
                float width = tempRect.width() * (1.0f - value) / 2;
                float height = tempRect.height() * (1.0f - value) / 2;
                currentRectF = new RectF(tempRect.left + width, tempRect.top + height, tempRect.right - width, tempRect.bottom - height);
                currentRadius = model.getRadius();
                invalidate();
            }
        });
        return showAnimator;
    }


}

