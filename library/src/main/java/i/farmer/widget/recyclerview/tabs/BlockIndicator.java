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

    public BlockIndicator(boolean includeGap, @ColorInt int color, int spacing) {
        super(false, includeGap, color, spacing);
    }

    @Override
    protected float getIndicatorSize(boolean horizontal, float start, float end) {
        return end - start + mItemSpacing;
    }

    @Override
    protected float getIndicatorGap(float size, float nextSize, float distance, float positionOffset) {
        return (nextSize - size) * positionOffset;
    }

    @Override
    protected void drawIndicator(RecyclerView parent, boolean selected, boolean horizontal, Canvas canvas, float center, float size) {
        if (horizontal) {
            float r = (parent.getBottom() - parent.getTop()) / 2;
            canvas.drawRoundRect(center - size / 2,
                    parent.getTop(),
                    center + size / 2,
                    parent.getBottom(),
                    r,
                    r,
                    mIndicatorPaint);
        } else {
            float r = (parent.getRight() - parent.getLeft()) / 2;
            canvas.drawRoundRect(parent.getLeft(),
                    center - size / 2,
                    parent.getRight(),
                    center + size / 2,
                    r,
                    r,
                    mIndicatorPaint);
        }
    }
}
