package i.farmer.widget.recyclerview.tabs;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author i-farmer
 * @created-time 2020/12/8 11:47 AM
 * @description 圆角矩形指示器
 */
public class RectangleIndicator extends RecyclerTabIndicator {
    private int mIndicatorWidth = 42;
    private int mIndicatorHeight = 12;
    private int scrollPosition;
    private float scrollPositionOffset;
    private Paint mIndicatorPaint;

    public RectangleIndicator() {
        mIndicatorPaint = new Paint();
        mIndicatorPaint.setStyle(Paint.Style.FILL);
        mIndicatorPaint.setAntiAlias(true);
        mIndicatorPaint.setColor(Color.parseColor("#000000"));
    }

    @Override
    public void scrollToTabIndicator(int position, float positionOffset) {
        this.scrollPosition = position;
        this.scrollPositionOffset = positionOffset;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent,
                           @NonNull RecyclerView.State state) {
        if (!(parent.getLayoutManager() instanceof LinearLayoutManager)) {
            throw new IllegalArgumentException("This LayoutManager is not supported.");
        }
        LinearLayoutManager mLayoutManager = (LinearLayoutManager) parent.getLayoutManager();
        int activePosition = mLayoutManager.findFirstVisibleItemPosition();
        if (activePosition == RecyclerView.NO_POSITION) {
            // 没有数据
            return;
        }
        final boolean horizontal = mLayoutManager.getOrientation() == RecyclerView.HORIZONTAL;   // 是否横向
        final int position = this.scrollPosition;
        int firstVisible = mLayoutManager.findFirstVisibleItemPosition();
        int lastVisible = mLayoutManager.findLastVisibleItemPosition();

        float positionStart, positionEnd, distance;   // distance，是position与position+1之间的中间点距离
        if (position < firstVisible) {
            // 不可见、往左
            if (position + 1 < firstVisible) {
                // 前后两个都不可见
                return;
            }
            View firstView = mLayoutManager.findViewByPosition(firstVisible);
            positionEnd = getStart(firstView, horizontal);
            positionStart = positionEnd - getSize(firstView, horizontal);     // 假设宽度相同
            distance = getSize(firstView, horizontal);
        } else if (position > lastVisible) {
            // 不可见、往右
            if (position - 1 > lastVisible) {
                // 前后两个都不可见
                return;
            }
            View lastView = mLayoutManager.findViewByPosition(lastVisible);
            positionStart = getEnd(lastView, horizontal);
            positionEnd = positionStart + getSize(lastView, horizontal);      // 假设宽度相同
            distance = getSize(lastView, horizontal);
        } else {
            // 可见
            View currentView = mLayoutManager.findViewByPosition(position);
            positionStart = getStart(currentView, horizontal);
            positionEnd = getEnd(currentView, horizontal);
            if (position + 1 > lastVisible) {
                // 下一个不可见
                distance = getSize(currentView, horizontal);                // 假设宽度相同
            } else {
                View nextView = mLayoutManager.findViewByPosition(position + 1);
                distance = (getSize(currentView, horizontal) + getSize(nextView, horizontal)) / 2;        // 计算两个中间点的距离
            }
        }
        final float positionOffset = this.scrollPositionOffset;
        final float size = horizontal ? mIndicatorWidth : mIndicatorHeight;     // 指示器大小
        float gap = (distance - size) * (positionOffset < 0.5 ? positionOffset : (1 - positionOffset));

        // 计算起始点
        float start = (positionStart + positionEnd) / 2 + distance * positionOffset - size / 2 - gap / 2;
        if (start < (positionStart + positionEnd) / 2 - size / 2) {
            start = (positionStart + positionEnd) / 2 - size / 2;
        } else if (start + size + gap > (positionStart + positionEnd) / 2 + distance + size / 2) {
            start = (positionStart + positionEnd) / 2 + distance + size / 2;
        }

        drawIndicator(parent, horizontal, c, start, size + gap);
    }

    private void drawIndicator(RecyclerView parent, boolean horizontal, Canvas canvas, float start, float size) {
        if (horizontal) {
            canvas.drawRoundRect(start,
                    parent.getBottom() - mIndicatorHeight,
                    start + size,
                    parent.getBottom(),
                    mIndicatorWidth / 2,
                    mIndicatorWidth / 2,
                    mIndicatorPaint);
        } else {
            canvas.drawRoundRect(parent.getRight() - mIndicatorWidth,
                    start,
                    parent.getRight(),
                    start + size,
                    mIndicatorHeight / 2,
                    mIndicatorHeight / 2,
                    mIndicatorPaint);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (!(parent.getLayoutManager() instanceof LinearLayoutManager)) {
            throw new IllegalArgumentException("This LayoutManager is not supported.");
        }
        LinearLayoutManager mLayoutManager = (LinearLayoutManager) parent.getLayoutManager();
        if (mLayoutManager.getOrientation() == RecyclerView.HORIZONTAL) {
            outRect.set(0, 0, 0, mIndicatorHeight);
        } else {
            outRect.set(0, 0, mIndicatorWidth, 0);
        }
    }
}
