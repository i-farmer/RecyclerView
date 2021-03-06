package i.farmer.widget.recyclerview.tabs;

import android.graphics.Canvas;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author i-farmer
 * @created-time 2020/12/10 4:49 PM
 * @description 块状背景指示器
 */
public class BlockIndicator extends RecyclerTabViewIndicator {
    private int indicatorPadding;

    public BlockIndicator(boolean includeGap, @ColorInt int color, int indicatorPadding, int spacing) {
        super(false, includeGap, color, spacing);
        this.indicatorPadding = indicatorPadding;
    }

    @Override
    protected float getIndicatorSize(boolean horizontal, float start, float end) {
        return end - start + indicatorPadding * 2;
    }

    @Override
    protected float getIndicatorGap(float size, float nextSize, float distance, float positionOffset) {
        return (nextSize - size) * positionOffset;
    }

    @Override
    protected void drawIndicator(RecyclerView parent, boolean selected, boolean horizontal, Canvas canvas, float center, float size) {
        if (horizontal) {
            float height = parent.getBottom() - parent.getTop();
            float r = height / 2.f;
            canvas.drawRoundRect(center - size / 2.f,
                    0,
                    center + size / 2.f,
                    height,
                    r,
                    r,
                    mIndicatorPaint);
        } else {
            float width = parent.getRight() - parent.getLeft();
            float r = width / 2.f;
            canvas.drawRoundRect(0,
                    center - size / 2.f,
                    width,
                    center + size / 2.f,
                    r,
                    r,
                    mIndicatorPaint);
        }
    }
}
