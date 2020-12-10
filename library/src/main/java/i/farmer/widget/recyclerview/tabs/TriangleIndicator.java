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
    private Path mIndicatorPath;

    public TriangleIndicator(@ColorInt int color, int width, int height, int spacing) {
        super(false, color, spacing);
        if (width > 0) {
            this.mIndicatorWidth = width;
        }
        if (height > 0) {
            this.mIndicatorHeight = height;
        }
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
    protected void drawIndicator(RecyclerView parent, boolean horizontal, Canvas canvas, float center, float size) {
        this.mIndicatorPath.reset();
        // 箭头
        if (horizontal) {
            this.mIndicatorPath.moveTo(center - size / 2, parent.getBottom());
            this.mIndicatorPath.lineTo(center + size / 2, parent.getBottom());
            this.mIndicatorPath.lineTo(center, parent.getBottom() - mIndicatorHeight);
            this.mIndicatorPath.close();
        } else {
            this.mIndicatorPath.moveTo(parent.getRight(), center - size / 2);
            this.mIndicatorPath.lineTo(parent.getRight(), center + size / 2);
            this.mIndicatorPath.lineTo(parent.getRight() - mIndicatorWidth, center);
            this.mIndicatorPath.close();
        }
        canvas.drawPath(this.mIndicatorPath, this.mIndicatorPaint);
    }
}
