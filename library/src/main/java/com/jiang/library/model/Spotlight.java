package com.jiang.library.model;

/**
 * Created by wuliao on 16/7/13.
 */
import android.animation.Animator;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/7/12 0012.
 */

public class Spotlight {
    private RectF rectF;
    private float radius;
    private Animator showAnimator;
    private Animator hideAnimator;
    private View showView;
    private ViewGroup.LayoutParams params;
    private View.OnClickListener listener;

    public View.OnClickListener getListener() {
        return listener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public View getShowView() {
        return showView;
    }

    public void setShowView(View showView) {
        this.showView = showView;
    }

    public ViewGroup.LayoutParams getParams() {
        return params;
    }

    public void setParams(ViewGroup.LayoutParams params) {
        this.params = params;
    }

    public Spotlight(RectF rectF, float radius) {
        this.rectF = rectF;

        this.radius = radius;
    }

    public RectF getRectF() {
        return rectF;
    }

    public void setRectF(RectF rectF) {
        this.rectF = rectF;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Animator getShowAnimator() {
        return showAnimator;
    }

    public void setShowAnimator(Animator showAnimator) {
        this.showAnimator = showAnimator;
    }

    public Animator getHideAnimator() {
        return hideAnimator;
    }

    public void setHideAnimator(Animator hideAnimator) {
        this.hideAnimator = hideAnimator;
    }


}

