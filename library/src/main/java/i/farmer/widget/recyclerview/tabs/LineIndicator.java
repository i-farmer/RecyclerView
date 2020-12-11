package i.farmer.widget.recyclerview.tabs;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author i-farmer
 * @created-time 2020/12/8 11:47 AM
 * @description 线条指示器
 */
class LineIndicator extends RecyclerTabViewIndicator {
    private int mIndicatorWidth = 42;
    private int mIndicatorHeight = 12;

    public LineIndicator(boolean includeGap, @ColorInt int color, int width, int height, int spacing) {
        super(true, includeGap, color, spacing);
        if (width > 0) {
            this.mIndicatorWidth = width;
        }
        if (height > 0) {
            this.mIndicatorHeight = height;
        }
    }

    @Override
    protected float getIndicatorSize(boolean horizontal, float start, float end) {
        return horizontal ? mIndicatorWidth : mIndicatorHeight;
    }

    @Override
    protected float getIndicatorGap(float size, float nextSize, float distance, float positionOffset) {
        return (distance - size) * (positionOffset < 0.5 ? positionOffset : (1 - positionOffset));
    }

    @Override
    protected void drawIndicator(RecyclerView parent, boolean selected, boolean horizontal, Canvas canvas, float center, float size) {
        if (horizontal) {
            float height = parent.getBottom() - parent.getTop();
            canvas.drawRoundRect(center - size / 2.f,
                    height - mIndicatorHeight,
                    center + size / 2.f,
                    height,
                    mIndicatorHeight / 2.f,
                    mIndicatorHeight / 2.f,
                    mIndicatorPaint);
        } else {
            float width = parent.getRight() - parent.getLeft();
            canvas.drawRoundRect(width - mIndicatorWidth,
                    center - size / 2.f,
                    width,
                    center + size / 2.f,
                    mIndicatorWidth / 2.f,
                    mIndicatorWidth / 2.f,
                    mIndicatorPaint);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        LinearLayoutManager mLayoutManager = (LinearLayoutManager) parent.getLayoutManager();
        if (mLayoutManager.getOrientation() == RecyclerView.HORIZONTAL) {
            outRect.set(0, 0, 0, mIndicatorHeight);
        } else {
            outRect.set(0, 0, mIndicatorWidth, 0);
        }
    }
}
