package com.jiang.library.widget;

/**
 * Created by wuliao on 16/7/13.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
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
    private boolean mIsAnimation;
    private RectF mCurrentRectF = new RectF(0, 0, 200, 200);
    private float mCurrentRadius;
    private Paint mPaint;
    //the mask
    private Bitmap mMaskBitmap;

    private List<Spotlight> mSpotlights;
    private Builder mBuilder;
    private Activity mActivity;
    private int mDurationTime = 300;
    public static int TOP = 0x0001;
    public static int LEFT = 0x0002;
    public static int RIGHT = 0x0004;
    public static int BOTTOM = 0x0008;
    public static final int CENTER_VERTICAL = 0x0010;
    public static final int CENTER_HORIZONTAL = 0x0020;

    public static int PARENT_TOP = 0x0040;
    public static int PARENT_LEFT = 0x0080;
    public static int PARENT_RIGHT = 0x0100;
    public static int PARENT_BOTTOM = 0x0200;

    public static int PARENT_TOP_LEFT = PARENT_LEFT | PARENT_TOP;
    public static int PARENT_BOTTOM_LEFT = PARENT_LEFT | PARENT_BOTTOM;
    public static int PARENT_TOP_RIGHT = PARENT_RIGHT | PARENT_TOP;
    public static int PARENT_BOTTOM_RIGHT = PARENT_RIGHT | PARENT_BOTTOM;
    public static int PARENT_CENTER = CENTER_VERTICAL | CENTER_HORIZONTAL;

    public enum Shape {
        CIRCLE,
        RECT
    }

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

    private void init() {
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mSpotlights = new ArrayList<>();
    }

    public Builder addGuideView(Activity activity, final View targetView) {
        this.mActivity = activity;
        if (targetView == null) {
            throw new IllegalArgumentException("targetView can not be null");
        }
        mBuilder = new Builder(targetView);
        return mBuilder;
    }

    private int mMaskColor = 0x77000000;

    public void setmMaskColor(int mMaskColor) {
        this.mMaskColor = mMaskColor;
    }

    private static final PorterDuffXfermode MODE_DST_OUT = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mCurrentRectF != null) {
            mMaskBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas myCanvas = new Canvas(mMaskBitmap);
            myCanvas.drawColor(mMaskColor);
            mPaint.setXfermode(MODE_DST_OUT);
            myCanvas.drawRoundRect(mCurrentRectF, mCurrentRadius, mCurrentRadius, mPaint);
            canvas.drawBitmap(mMaskBitmap, 0, 0, new Paint());
        }
        super.dispatchDraw(canvas);
    }

    private boolean mIsPrepare;

    public void prepare() {
        mIsPrepare = true;
        int size = mSpotlights.size();
        for (int i = 0; i < size; i++) {
            final Spotlight model = mSpotlights.get(i);
            if (model.getShowAnimator() == null) {//没有开始动画,一般是第一个
                ValueAnimator showAnimator = getShowAnimator(model);
                mCurrentRadius = model.getRadius();
                model.setShowAnimator(showAnimator);
            }
            if (i + 1 < size) {//not the last
                final Spotlight nextModel = mSpotlights.get(i + 1);
                if (nextModel.getRadius() == model.getRadius()) {
                    ValueAnimator showAnimator = ValueAnimator.ofObject(new RectFEvaluator(), model.getRectF(), nextModel.getRectF());
                    showAnimator.setDuration(mDurationTime);
                    showAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            RectF tempRect = (RectF) valueAnimator.getAnimatedValue();
                            mCurrentRectF = tempRect;
                            invalidate();
                        }
                    });
                    bindAnimatorStatus(showAnimator);
                    nextModel.setShowAnimator(showAnimator);
                    model.setHideAnimator(null);
                } else {
                    model.setHideAnimator(getHideAnimator(model));
                    nextModel.setShowAnimator(getShowAnimator(nextModel));
                }
                model.setListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mIsAnimation) return;
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
                        if (mIsAnimation) return;
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
        if (!mIsPrepare) {
            prepare();
        }
        ViewGroup.LayoutParams guideParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        final ViewGroup contentView = (ViewGroup) mActivity.getWindow().getDecorView();

        if (getParent() == null) {
            contentView.addView(SpotlightView.this, guideParams);
        }

        final Spotlight model = mSpotlights.get(0);
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
        begin.start();
    }

    public void clearGuide() {
        if (mActivity != null) {
            final ViewGroup contentView = (ViewGroup) mActivity.getWindow().getDecorView();
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
        private float horizontalPadding = 6;
        private float verticalPadding = 6;
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
                        float diameter = Math.max(targetWidth, targetHeight) + 2 * horizontalPadding;
                        float x = location[0] + targetWidth / 2.0f;
                        float y = location[1] + targetHeight / 2.0f;
                        radius = (int) (diameter / 2);
                        rectF = new RectF(x - diameter / 2, y - diameter / 2, x + diameter / 2, y + diameter / 2);
                    } else {
                        rectF = new RectF(location[0] - horizontalPadding, location[1] - verticalPadding,
                                location[0] + targetWidth + horizontalPadding, location[1] + targetHeight + verticalPadding);
                    }
                    Spotlight light = new Spotlight(rectF, radius);
                    light.setParams(params);
                    light.setShowView(showView);
                    if (position > mSpotlights.size()) {
                        mSpotlights.add(light);
                    } else {
                        mSpotlights.add(position, light);
                    }
                    targetView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
            mBuilder = null;
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

        /**
         * make showView move horizontal
         *
         * @param leftMargin
         * @return
         */
        public Builder setLeftMargin(int leftMargin) {
            this.leftMargin = leftMargin;
            return this;
        }

        /**
         * make showView move vertical
         *
         * @param topMargin
         * @return
         */
        public Builder setTopMargin(int topMargin) {
            this.topMargin = topMargin;
            return this;
        }

        /**
         * the shape of mask
         *
         * @param shape
         * @return
         */
        public Builder setShape(Shape shape) {
            this.shape = shape;
            return this;
        }

        public Builder setPadding(int padding) {
            this.horizontalPadding = padding;
            this.verticalPadding = padding;
            return this;
        }

        public Builder setHorizontalPadding(int horizontalPadding) {
            this.horizontalPadding = horizontalPadding;
            return this;
        }

        public Builder setVerticalPadding(int verticalPadding) {
            this.verticalPadding = verticalPadding;
            return this;
        }

        public Builder setRadius(int radius) {
            this.radius = radius;
            return this;
        }

    }

    /**
     * default show animation
     *
     * @param model
     * @return
     */
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
                mCurrentRectF = new RectF(tempRect.left + width, tempRect.top + height,
                        tempRect.right - width, tempRect.bottom - height);
                mCurrentRadius = model.getRadius();
                invalidate();
            }
        });
        bindAnimatorStatus(showAnimator);
        return showAnimator;
    }

    /**
     * default hide animation
     *
     * @param model
     * @return
     */
    private ValueAnimator getHideAnimator(final Spotlight model) {
        ValueAnimator hideAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
        hideAnimator.setDuration(mDurationTime);
        hideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                RectF tempRect = model.getRectF();
                float value = (float) valueAnimator.getAnimatedValue();
                float width = tempRect.width() * (1.0f - value) / 2;
                float height = tempRect.height() * (1.0f - value) / 2;
                mCurrentRectF = new RectF(tempRect.left + width, tempRect.top + height,
                        tempRect.right - width, tempRect.bottom - height);
                mCurrentRadius = model.getRadius();
                invalidate();
            }
        });
        bindAnimatorStatus(hideAnimator);
        return hideAnimator;
    }

    private void bindAnimatorStatus(ValueAnimator showAnimator) {
        showAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mIsAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsAnimation = false;
            }
        });
    }

}

