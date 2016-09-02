package com.jiang.library.widget;

import android.animation.TypeEvaluator;
import android.graphics.RectF;

/**
 * Created by Administrator on 2016/9/3 0003.
 */

public class RectFEvaluator implements TypeEvaluator<RectF> {

    @Override
    public RectF evaluate(float fraction, RectF startValue, RectF endValue) {
        float startLeft = startValue.left;
        float endLeft = endValue.left;
        float startTop = startValue.top;
        float endTop = endValue.top;

        float startRight = startValue.right;
        float endRight = endValue.right;
        float startBottom = startValue.bottom;
        float endBottom = endValue.bottom;

        float left = startLeft + fraction * (endLeft - startLeft);
        float top = startTop + fraction * (endTop - startTop);
        float right = startRight + fraction * (endRight - startRight);
        float bottom = startBottom + fraction * (endBottom - startBottom);
        return new RectF(left, top, right, bottom);
    }
}
