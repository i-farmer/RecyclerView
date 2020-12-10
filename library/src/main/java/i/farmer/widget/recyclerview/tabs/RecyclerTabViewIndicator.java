package i.farmer.widget.recyclerview.tabs;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author i-farmer
 * @created-time 2020/12/8 4:49 PM
 * @description 指示器
 */
abstract class RecyclerTabViewIndicator extends RecyclerView.ItemDecoration {
    private boolean drawOver;
    private boolean includeGap;                 // 指示器滑动过程中是否包含gap差值计算
    private int scrollPosition;                 // 当前position
    private float scrollPositionOffset;         // 当前position偏移量
    protected int mItemSpacing = 0;

    protected Paint mIndicatorPaint;

    public RecyclerTabViewIndicator(boolean drawOver, boolean includeGap, @ColorInt int color, int spacing) {
        this.drawOver = drawOver;
        mIndicatorPaint = new Paint();
        mIndicatorPaint.setStyle(Paint.Style.FILL);
        mIndicatorPaint.setAntiAlias(true);
        mIndicatorPaint.setColor(color);
        this.includeGap = includeGap;
        if (spacing > 0) {
            this.mItemSpacing = spacing;
        }
    }

    public void scrollToTabIndicator(int position, float positionOffset) {
        this.scrollPosition = position;
        this.scrollPositionOffset = positionOffset;
    }

    /**
     * 设置指示器颜色
     *
     * @param color
     */
    public void setIndicatorColor(@ColorInt int color) {
        this.mIndicatorPaint.setColor(color);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (!(parent.getLayoutManager() instanceof LinearLayoutManager)) {
            throw new IllegalArgumentException("This LayoutManager is not supported.");
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (!drawOver) {
            onDrawIndicator(c, parent, state);
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent,
                           @NonNull RecyclerView.State state) {
        if (drawOver) {
            onDrawIndicator(c, parent, state);
        }
    }

    private void onDrawIndicator(@NonNull Canvas c, @NonNull RecyclerView parent,
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
        final float positionOffset = this.scrollPositionOffset;
        final int firstVisible = mLayoutManager.findFirstVisibleItemPosition();
        final int lastVisible = mLayoutManager.findLastVisibleItemPosition();

        float positionStart, positionEnd, positionNextStart, positionNextEnd;
        if (position < firstVisible) {
            // 不可见、往左
            if (position + 1 < firstVisible) {
                // 前后两个都不可见
                return;
            }
            View firstView = mLayoutManager.findViewByPosition(firstVisible);
            positionNextStart = getStart(firstView, horizontal);
            positionNextEnd = getEnd(firstView, horizontal);
            positionEnd = positionNextStart - mItemSpacing;                         // 需要考虑间隔
            positionStart = positionEnd - getSize(firstView, horizontal);           // 假设宽度相同
        } else if (position > lastVisible) {
            // 不可见、往右
            if (position - 1 > lastVisible) {
                // 前后两个都不可见
                return;
            }
            View lastView = mLayoutManager.findViewByPosition(lastVisible);
            positionStart = getEnd(lastView, horizontal) + mItemSpacing;            // 需要考虑间隔
            positionEnd = positionStart + getSize(lastView, horizontal);            // 假设宽度相同
            positionNextStart = positionEnd + mItemSpacing;                         // 需要考虑间隔
            positionNextEnd = positionNextStart + getSize(lastView, horizontal);    // 假设宽度相同
        } else {
            // 可见
            View currentView = mLayoutManager.findViewByPosition(position);
            positionStart = getStart(currentView, horizontal);
            positionEnd = getEnd(currentView, horizontal);
            if (position + 1 > lastVisible) {
                // 下一个不可见
                positionNextStart = positionEnd + mItemSpacing;                     // 需要考虑间隔
                positionNextEnd = positionNextStart + getSize(currentView, horizontal); // 假设宽度相同
            } else {
                View nextView = mLayoutManager.findViewByPosition(position + 1);
                positionNextStart = getStart(nextView, horizontal);
                positionNextEnd = getEnd(nextView, horizontal);
            }
        }
        // distance，是position与position+1之间的中间点距离，另外加上item间隔
        final float distance = (positionNextStart + positionNextEnd - positionStart - positionEnd) / 2.f;
        // 当前指示器大小
        final float size = getIndicatorSize(horizontal, positionStart, positionEnd);
        // 下一个指示器大小
        final float nextSize = getIndicatorSize(horizontal, positionNextStart, positionNextEnd);
        // 指示器差值
        float gap = 0;
        if (includeGap) {
            gap = getIndicatorGap(size, nextSize, distance, positionOffset);
        }

        // 计算指示器中间点
        float center = (positionEnd + positionStart) / 2.f + distance * positionOffset;

        drawIndicator(parent, positionOffset == 0, horizontal, c, center, size + gap);
    }

    /**
     * 获取指示器大小
     *
     * @param horizontal 排版方向
     * @param start      positionView的起始点
     * @param end        positionView的结束点、nextView的起始点
     * @return
     */
    protected abstract float getIndicatorSize(boolean horizontal, float start, float end);

    /**
     * 获取指示器在滑动过程中的size变化差值
     *
     * @param size           指示器大小
     * @param nextSize       指示器大小
     * @param distance
     * @param positionOffset
     * @return
     */
    protected abstract float getIndicatorGap(float size, float nextSize, float distance, float positionOffset);

    /**
     * 绘制指示器
     *
     * @param parent
     * @param selected   是否选中
     * @param horizontal
     * @param canvas
     * @param center
     * @param size
     */
    protected abstract void drawIndicator(RecyclerView parent, boolean selected, boolean horizontal, Canvas canvas, float center, float size);

    private void checkView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("The view can not be null!");
        }
    }

    protected float getStart(View view, boolean horizontal) {
        checkView(view);
        if (horizontal) {
            return view.getLeft();
        } else {
            return view.getTop();
        }
    }

    protected float getEnd(View view, boolean horizontal) {
        checkView(view);
        if (horizontal) {
            return view.getRight();
        } else {
            return view.getBottom();
        }
    }

    protected float getSize(View view, boolean horizontal) {
        checkView(view);
        if (horizontal) {
            return view.getRight() - view.getLeft();
        } else {
            return view.getBottom() - view.getTop();
        }
    }
}
