package i.farmer.widget.recyclerview.tabs;

import android.graphics.Canvas;
import android.graphics.Path;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author i-farmer
 * @created-time 2020/12/10 4:23 PM
 * @description 三角指示器
 */
class TriangleIndicator extends RecyclerTabViewIndicator {
    private int mIndicatorWidth = 42;
    private int mIndicatorHeight = 12;
    private boolean mIndicatorSmoothCircle;
    private Path mIndicatorPath;

    public TriangleIndicator(boolean smoothCircle, @ColorInt int color, int width, int height, int spacing) {
        super(false, color, spacing);
        if (width > 0) {
            this.mIndicatorWidth = width;
        }
        if (height > 0) {
            this.mIndicatorHeight = height;
        }
        this.mIndicatorSmoothCircle = smoothCircle;
        this.mIndicatorPath = new Path();
    }

    @Override
    protected float getIndicatorSize(boolean horizontal, float start, float end) {
        return horizontal ? mIndicatorWidth : mIndicatorHeight;
    }

    @Override
    protected float getIndicatorGap(float size, float nextSize, float distance, float positionOffset) {
        return 0;
    }

    @Override
    protected void drawIndicator(RecyclerView parent, boolean selected, boolean horizontal, Canvas canvas, float center, float size) {
        if (selected || !this.mIndicatorSmoothCircle) {
            // 箭头
            this.mIndicatorPath.reset();
            if (horizontal) {
                this.mIndicatorPath.moveTo(center - size / 2, parent.getBottom());
                this.mIndicatorPath.lineTo(center + size / 2, parent.getBottom());
                this.mIndicatorPath.lineTo(center, parent.getBottom() - mIndicatorHeight);
            } else {
                this.mIndicatorPath.moveTo(parent.getRight(), center - size / 2);
                this.mIndicatorPath.lineTo(parent.getRight(), center + size / 2);
                this.mIndicatorPath.lineTo(parent.getRight() - mIndicatorWidth, center);
            }
            this.mIndicatorPath.close();
            canvas.drawPath(this.mIndicatorPath, this.mIndicatorPaint);
        } else {
            // 滑动过程中
            if (horizontal) {
                float r = mIndicatorHeight / 2;
                canvas.drawCircle(center, parent.getBottom() - r, r, this.mIndicatorPaint);
            } else {
                float r = mIndicatorWidth / 2;
                canvas.drawCircle(parent.getRight() - r, center, r, this.mIndicatorPaint);
            }
        }
    }
}
