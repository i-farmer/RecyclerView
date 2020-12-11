package i.farmer.widget.recyclerview.tabs;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
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
        super(true, false, color, spacing);
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
                float height = parent.getBottom() - parent.getTop();
                this.mIndicatorPath.moveTo(center - size / 2.f, height);
                this.mIndicatorPath.lineTo(center + size / 2.f, height);
                this.mIndicatorPath.lineTo(center, height - mIndicatorHeight);
            } else {
                float width = parent.getRight() - parent.getLeft();
                this.mIndicatorPath.moveTo(width, center - size / 2.f);
                this.mIndicatorPath.lineTo(width, center + size / 2.f);
                this.mIndicatorPath.lineTo(width - mIndicatorWidth, center);
            }
            this.mIndicatorPath.close();
            canvas.drawPath(this.mIndicatorPath, this.mIndicatorPaint);
        } else {
            // 滑动过程中
            if (horizontal) {
                float height = parent.getBottom() - parent.getTop();
                float r = mIndicatorHeight / 2.f;
                canvas.drawCircle(center, height - r, r, this.mIndicatorPaint);
            } else {
                float width = parent.getRight() - parent.getLeft();
                float r = mIndicatorWidth / 2.f;
                canvas.drawCircle(width - r, center, r, this.mIndicatorPaint);
            }
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        LinearLayoutManager mLayoutManager = (LinearLayoutManager) parent.getLayoutManager();
        if (mLayoutManager.getOrientation() == RecyclerView.HORIZONTAL) {
            outRect.set(0, 0, 0, mIndicatorHeight);
        } else {
            outRect.set(0, 0, mIndicatorWidth, 0);
        }
    }
}
