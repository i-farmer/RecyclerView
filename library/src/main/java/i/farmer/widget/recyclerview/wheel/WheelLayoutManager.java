package i.farmer.widget.recyclerview.wheel;

import android.graphics.PointF;
import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author i-farmer
 * @created-time 2020/11/24 11:12 AM
 * @description wheel滚轮用
 * <p>
 */
public class WheelLayoutManager extends RecyclerView.LayoutManager implements
        RecyclerView.SmoothScroller.ScrollVectorProvider {
    /**
     * 选中
     */
    public interface OnItemSelectedListener {
        void onItemSelected(int position);  // 选中
    }

    /**
     * 当item填充或者滚动的时候回调
     */
    public interface OnItemFillListener {
        void onItemSelected(View itemView, int position);

        void onItemUnSelected(View itemView, int position);
    }

    public static final int HORIZONTAL = RecyclerView.HORIZONTAL;
    public static final int VERTICAL = RecyclerView.VERTICAL;
    private final int FILL_START = -1;
    private final int FILL_END = 1;

    private int mOrientation;           // 摆放子View的方向，默认竖向
    private int mVisibleCount;          // 可见数量
    private boolean mIsLoop;            // 是否循环滚动
    private boolean mIsScale;           // 缩放系数
    private float mAlpha;               // 未选中item的透明度

    private int mPendingFillPosition = RecyclerView.NO_POSITION;        // 要绘制的第一个视图position
    private int mPendingScrollToPosition = RecyclerView.NO_POSITION;    // 将要scrollTo的视图position

    private int mItemWidth = 0;         // 保存下item的width和height，计算RecyclerView宽高用
    private int mItemHeight = 0;

    private Set<View> mRecycleViews = new HashSet<>();                  // 要回收的View先缓存起来
    private LinearSnapHelper mSnapHelper = new LinearSnapHelper();      // 直接搞个SnapHelper来findCenterView

    // 选中中间item的监听器的集合
    private List<OnItemSelectedListener> mOnItemSelectedListener = null;
    // 子view填充或滚动监听器的集合
    private List<OnItemFillListener> mOnItemFillListener = null;
    // 用于LayoutManager的帮助器类，用于根据视图的方向抽象度量
    private OrientationHelper mOrientationHelper;

    private WheelLayoutManager(int orientation, int visibleCount, boolean isLoop,
                               boolean scale,
                               @FloatRange(from = 0.0, to = 1.0) float alpha) {
        if (visibleCount % 2 == 0) {
            throw new IllegalArgumentException("visibleCount == $visibleCount 不能是偶数");
        }
        this.mOrientation = orientation;
        this.mVisibleCount = visibleCount;
        this.mIsLoop = isLoop;
        this.mIsScale = scale;
        this.mAlpha = alpha;
    }

    private OrientationHelper getDefaultOrientationHelper() {
        if (null == mOrientationHelper) {
            if (mOrientation == HORIZONTAL) {
                mOrientationHelper = OrientationHelper.createHorizontalHelper(this);
            } else {
                mOrientationHelper = OrientationHelper.createVerticalHelper(this);
            }
        }
        return mOrientationHelper;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        if (mOrientation == HORIZONTAL) {
            return new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.WRAP_CONTENT,
                    RecyclerView.LayoutParams.MATCH_PARENT
            );
        } else {
            return new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            );
        }
    }

    @Override
    public boolean isAutoMeasureEnabled() {
        return false;   // 返回false，自己实现测量
    }

    @Override
    public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state,
                          int widthSpec, int heightSpec) {
        if (state.getItemCount() <= 0) {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
            return;
        }
        if (state.isPreLayout()) {
            return;
        }
        // 用第一个View计算高度，这种方式可能不太好
        View itemView = recycler.getViewForPosition(0);
        addView(itemView);
        // 这里不能用measureChild方法，具体看内部源码实现，内部getWidth默认为0
//        measureChildWithMargins(itemView, 0, 0)
        itemView.measure(widthSpec, heightSpec);
        mItemWidth = getDecoratedMeasuredWidth(itemView);
        mItemHeight = getDecoratedMeasuredHeight(itemView);
        // 回收
        detachAndScrapView(itemView, recycler);

        // 设置宽高
        if (mOrientation == HORIZONTAL) {
            setMeasuredDimension(mItemWidth * mVisibleCount, mItemHeight);
        } else {
            setMeasuredDimension(mItemWidth, mItemHeight * mVisibleCount);
        }
    }

    /**
     * 软键盘的弹出和收起、scrollToPosition， 都会再次调用这个方法，自己要记录好偏移量
     *
     * @param recycler
     * @param state
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() <= 0) {
            // 如果itemCount<=0了，直接移除全部view
            removeAndRecycleAllViews(recycler);
            return;
        }
        if (state.isPreLayout()) {
            // 不支持预测动画，直接return
            return;
        }
        mPendingFillPosition = 0;   // 计算当前开始的position
        boolean isScrollTo = mPendingScrollToPosition != RecyclerView.NO_POSITION;
        if (isScrollTo) {
            mPendingFillPosition = mPendingScrollToPosition;
        } else if (getChildCount() > 0) {
            // 说明已经初始化过了，显示当前滚动到的position
            mPendingFillPosition = getSelectedPosition();
        }
        if (mPendingFillPosition >= state.getItemCount()) {
            // 解决当调用notifyDataChanges时itemCount变小
            // 且getSelectedPosition>itemCount的bug
            mPendingFillPosition = state.getItemCount() - 1;
        }

        // 暂时移除全部view，然后重新fill进来
        detachAndScrapAttachedViews(recycler);

        // 开始就向下填充
        int anchor = getOffsetSpace();  // 开始坐标
        int fillDirection = FILL_END;
        fillLayout(recycler, state, anchor, fillDirection);
        // 如果是isLoop=true，或者是scrollTo或软键盘弹起，再向上填充
        // getAnchorView可能为null，先判断下childCount
        if (getChildCount() > 0) {
            fillDirection = FILL_START;
            mPendingFillPosition = getPendingFillPosition(fillDirection);
            anchor = getAnchor(fillDirection);
            fillLayout(recycler, state, anchor, fillDirection);
        }

        if (isScrollTo) {
            // scrollTo过来的要回调onItemSelected
            int centerPosition = getSelectedPosition();
            dispatchOnItemSelectedListener(centerPosition);
        }

        // 变换children
        transformChildren();
        // 分发Item Fill事件
        dispatchOnItemFillListener();
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        // 恢复默认值
        mPendingScrollToPosition = RecyclerView.NO_POSITION;
    }

    @Override
    public boolean canScrollHorizontally() {
        return mOrientation == HORIZONTAL;
    }

    @Override
    public boolean canScrollVertically() {
        return mOrientation == VERTICAL;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mOrientation == VERTICAL) {
            return 0;
        }
        return scrollBy(dx, recycler, state);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mOrientation == HORIZONTAL) {
            return 0;
        }
        return scrollBy(dy, recycler, state);
    }

    @Override
    public void scrollToPosition(int position) {
        if (getChildCount() <= 0) {
            return;
        }
        checkToPosition(position);  // 校验一下position的合法性
        mPendingScrollToPosition = position;
        requestLayout();    // 重新调用onLayoutChildren
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        if (getChildCount() <= 0) {
            return;
        }
        checkToPosition(position);  // 校验一下position的合法性

        int toPosition = fixSmoothToPosition(position);

        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(toPosition);
        startSmoothScroll(smoothScroller);
    }

    /**
     * 配合 smoothScrollToPosition 滚动用
     *
     * @param targetPosition
     * @return
     * @see RecyclerView.SmoothScroller.ScrollVectorProvider
     */
    @Nullable
    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        if (getChildCount() <= 0) {
            return null;
        }
        int firstChildPos = getPosition(mSnapHelper.findSnapView(this));
        int direction = targetPosition < firstChildPos ? -1 : 1;
        if (mOrientation == HORIZONTAL) {
            return new PointF(direction, 0f);
        } else {
            return new PointF(0f, direction);
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (getChildCount() <= 0) {
            return;
        }
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            View centerView = getSelectedView();
            if (null == centerView) {
                return;
            }
            int centerPosition = getPosition(centerView);
            scrollToCenter(centerView, centerPosition); // 选中便宜恢复
        }
    }

    /**
     * 填充数据
     *
     * @param recycler
     * @param state
     * @param anchor
     * @param fillDirection
     */
    private void fillLayout(RecyclerView.Recycler recycler, RecyclerView.State state,
                            int anchor, int fillDirection) {
        int innerAnchor = anchor;
        int count = fillDirection == FILL_START ? getOffsetCount() : getFixVisibleCount();
        while (count > 0 && hasMore(state)) {
            View child = nextView(recycler, fillDirection);
            if (fillDirection == FILL_START) {
                addView(child, 0);
            } else {
                addView(child);
            }
            measureChildWithMargins(child, 0, 0);
            layoutChunk(child, innerAnchor, fillDirection);
            if (fillDirection == FILL_START) {
                innerAnchor -= getDefaultOrientationHelper().getDecoratedMeasurement(child);
            } else {
                innerAnchor += getDefaultOrientationHelper().getDecoratedMeasurement(child);
            }
            count--;
        }
    }

    /**
     * 获取偏移的item count
     * 例如：开始position == 0居中，就要偏移一个item count的距离
     */
    private int getOffsetCount() {
        return (mVisibleCount - 1) / 2;
    }

    /**
     * 获取真实可见的visible count
     * 例如：传入的visible count=3，但是在isLoop=false的情况下，
     * 开始只用填充2个item view进来就行了
     */
    private int getFixVisibleCount() {
        if (mIsLoop) {
            return mVisibleCount;
        }
        return (mVisibleCount + 1) / 2;
    }

    /**
     * 摆放item view
     */
    private void layoutChunk(View child, int anchor, int fillDirection) {
        int left, top, right, bottom;
        if (mOrientation == HORIZONTAL) {
            top = getPaddingTop();
            bottom = getPaddingTop() + getDefaultOrientationHelper().getDecoratedMeasurementInOther(child) - getPaddingBottom();
            if (fillDirection == FILL_START) {
                right = anchor;
                left = right - getDefaultOrientationHelper().getDecoratedMeasurement(child);
            } else {
                left = anchor;
                right = left + getDefaultOrientationHelper().getDecoratedMeasurement(child);
            }
        } else {
            left = getPaddingLeft();
            right = getDefaultOrientationHelper().getDecoratedMeasurementInOther(child) - getPaddingRight();
            if (fillDirection == FILL_START) {
                bottom = anchor;
                top = bottom - getDefaultOrientationHelper().getDecoratedMeasurement(child);
            } else {
                top = anchor;
                bottom = anchor + getDefaultOrientationHelper().getDecoratedMeasurement(child);
            }
        }

        layoutDecoratedWithMargins(child, left, top, right, bottom);
    }

    /**
     * 滑动的统一处理事件
     *
     * @param delta    偏移量
     * @param recycler
     * @param state
     */
    private int scrollBy(int delta, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() <= 0 || delta == 0) {
            return 0;
        }

        // 开始填充item view
        int consume = fillScroll(delta, recycler, state);
        // 移动全部子view
        getDefaultOrientationHelper().offsetChildren(-consume);
        // 回收屏幕外的view
        recycleChildren(delta, recycler);
        // 变换children
        transformChildren();
        // 分发事件
        dispatchOnItemFillListener();

        return consume;
    }

    /**
     * 在滑动的时候填充view，
     * delta > 0 向右或向下移动
     * delta < 0 向左或向上移动
     */
    private int fillScroll(int delta, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int absDelta = Math.abs(delta);
        int remainSpace = Math.abs(delta);

        int fillDirection = delta > 0 ? FILL_END : FILL_START;

        // 检查滚动距离是否可以填充下一个view
        if (canNotFillScroll(fillDirection, absDelta)) {
            return delta;
        }

        // 检查是否滚动到了顶部或者底部
        if (checkScrollToEdge(fillDirection, state)) {
            int fixLastScroll = getFixLastScroll(fillDirection);
            return fillDirection == FILL_START ? Math.max(fixLastScroll, delta) : Math.min(fixLastScroll, delta);
        }

        // 获取将要填充的view
        mPendingFillPosition = getPendingFillPosition(fillDirection);

        while (remainSpace > 0 && hasMore(state)) {
            int anchor = getAnchor(fillDirection);
            View child = nextView(recycler, fillDirection);
            if (fillDirection == FILL_START) {
                addView(child, 0);
            } else {
                addView(child);
            }
            measureChildWithMargins(child, 0, 0);
            layoutChunk(child, anchor, fillDirection);
            remainSpace -= mOrientationHelper.getDecoratedMeasurement(child);
        }

        return delta;
    }

    /**
     * 如果anchorView的(start或end)+delta还是没出现在屏幕内，
     * 就继续滚动，不填充view
     */
    private boolean canNotFillScroll(int fillDirection, int delta) {
        View anchorView = getAnchorView(fillDirection);
        if (fillDirection == FILL_START) {
            int start = mOrientationHelper.getDecoratedStart(anchorView);
            return start + delta < mOrientationHelper.getStartAfterPadding();
        } else {
            int end = mOrientationHelper.getDecoratedEnd(anchorView);
            return end - delta > mOrientationHelper.getEndAfterPadding();
        }
    }

    /**
     * 检查是否滚动到了底部或者顶部
     */
    private boolean checkScrollToEdge(int fillDirection, RecyclerView.State state) {
        if (mIsLoop) {
            return false;
        }
        int anchorPosition = getAnchorPosition(fillDirection);
        return anchorPosition == 0 || anchorPosition == (state.getItemCount() - 1);
    }

    private int getFixLastScroll(int fillDirection) {
        View anchorView = getAnchorView(fillDirection);
        if (fillDirection == FILL_START) {
            return getDefaultOrientationHelper().getDecoratedStart(anchorView)
                    - mOrientationHelper.getStartAfterPadding() - getOffsetSpace();
        } else {
            return getDefaultOrientationHelper().getDecoratedEnd(anchorView)
                    - mOrientationHelper.getEndAfterPadding() + getOffsetSpace();
        }
    }

    /**
     * 如果不是循环模式，将要填充的view的position不在合理范围内
     * 就返回false
     */
    private boolean hasMore(RecyclerView.State state) {
        if (mIsLoop) {
            return true;
        }
        return mPendingFillPosition >= 0 && mPendingFillPosition < state.getItemCount();
    }

    /**
     * 获取锚点view，fill_end是最后一个，fill_start是第一个
     */
    private View getAnchorView(int fillDirection) {
        if (fillDirection == FILL_START) {
            return getChildAt(0);
        } else {
            return getChildAt(getChildCount() - 1);
        }
    }

    /**
     * 获取锚点view的position
     */
    private int getAnchorPosition(int fillDirection) {
        return getPosition(getAnchorView(fillDirection));
    }

    /**
     * 获取要开始填充的锚点位置
     */
    private int getAnchor(int fillDirection) {
        View anchorView = getAnchorView(fillDirection);
        if (fillDirection == FILL_START) {
            return getDefaultOrientationHelper().getDecoratedStart(anchorView);
        } else {
            return getDefaultOrientationHelper().getDecoratedEnd(anchorView);
        }
    }

    /**
     * 获取将要填充的view的position
     */
    private int getPendingFillPosition(int fillDirection) {
        return getAnchorPosition(fillDirection) + fillDirection;
    }

    /**
     * 获取下一个view，fill_start就-1，fill_end就是+1
     */
    private View nextView(RecyclerView.Recycler recycler, int fillDirection) {
        View child = getViewForPosition(recycler, mPendingFillPosition);
        mPendingFillPosition += fillDirection;
        return child;
    }

    /**
     * 回收在屏幕外的item view
     */
    private void recycleChildren(int delta, RecyclerView.Recycler recycler) {
        if (delta > 0) {
            recycleStart();
        } else {
            recycleEnd();
        }

        for (View view : mRecycleViews) {
            removeAndRecycleView(view, recycler);
        }
        mRecycleViews.clear();
    }

    /**
     * 向右或向下移动时，就回收前面部分超出屏幕的子view
     */
    private void recycleStart() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int end = getDefaultOrientationHelper().getDecoratedEnd(child);
            if (end < getDefaultOrientationHelper().getStartAfterPadding() - getItemOffset()) {
                mRecycleViews.add(child);
            } else {
                break;
            }
        }
    }

    /**
     * 向左或向上移动时，就回收后面部分超出屏幕的子view
     */
    private void recycleEnd() {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            int start = getDefaultOrientationHelper().getDecoratedStart(child);
            if (start > getDefaultOrientationHelper().getEndAfterPadding() + getItemOffset()) {
                mRecycleViews.add(child);
            } else {
                break;
            }
        }
    }

    /**
     * 获取居中被选中的view
     */
    private View getSelectedView() {
        return mSnapHelper.findSnapView(this);
    }

    /**
     * 获取一个item占用的空间，横向为宽，竖向为高
     */
    public int getItemSpace() {
        if (mOrientation == HORIZONTAL) {
            return mItemWidth;
        } else {
            return mItemHeight;
        }
    }

    /**
     * 增加一个偏移量让滚动顺滑点
     */
    private int getItemOffset() {
        return getItemSpace() / 2;
    }

    /**
     * 获取开始item距离开始位置的偏移量
     * 或者结束item距离尾端的偏移量
     */
    public int getOffsetSpace() {
        return getOffsetCount() * getItemSpace();
    }


    /**
     * 根据position获取一个item view
     */
    private View getViewForPosition(RecyclerView.Recycler recycler, int position) {
        if (!mIsLoop && (position < 0 || position >= getItemCount())) {
            throw new IllegalArgumentException("position <0 or >= itemCount with !isLoop");
        }

        //假设itemCount==100
        //[0,99] -- 100=0,101=1,102=2
        if (mIsLoop && position > getItemCount() - 1) {
            return recycler.getViewForPosition(position % getItemCount());
        }

        //[0,99] -- -1=99,-2=98,-3=97...-99=1,-100=0
        //              -101=99(-1)
        if (mIsLoop && position < 0) {
            return recycler.getViewForPosition(getItemCount() + (position % getItemCount()));
        }

        return recycler.getViewForPosition(position);
    }

    /**
     * 检查toPosition是否合法
     */
    private void checkToPosition(int position) {
        if (position < 0 || position > getItemCount() - 1) {
            throw new IllegalArgumentException("position is $position,must be >= 0 and < itemCount,");
        }
    }

    /**
     * 因为scrollTo是要居中，所以这里要fix一下
     */
    private int fixSmoothToPosition(int toPosition) {
        int fixCount = getOffsetCount();
        int centerPosition = getSelectedPosition();
        return toPosition + (centerPosition < toPosition ? fixCount : -fixCount);
    }


    /**
     * 分发回调OnItemSelectedListener
     */
    private void dispatchOnItemSelectedListener(int position) {
        if (null == mOnItemSelectedListener || mOnItemSelectedListener.isEmpty()) return;

        for (OnItemSelectedListener listener : mOnItemSelectedListener) {
            listener.onItemSelected(position);
        }
    }

    /**
     * 滚动到中间的item
     */
    private void scrollToCenter(View centerView, int centerPosition) {
        int destination =
                getDefaultOrientationHelper().getTotalSpace() / 2 - getDefaultOrientationHelper().getDecoratedMeasurement(
                        centerView
                ) / 2;
        int distance = destination - getDefaultOrientationHelper().getDecoratedStart(centerView);

        // 平滑动画的滚动到中心
//        smoothOffsetChildren(distance, centerPosition)
        // 直接滚动到中心
        mOrientationHelper.offsetChildren(distance);
        dispatchOnItemSelectedListener(centerPosition);
    }

    /**
     * 添加中心item选中的监听器
     */
    public void addOnItemSelectedListener(OnItemSelectedListener listener) {
        if (null == mOnItemSelectedListener) {
            mOnItemSelectedListener = new ArrayList<>();
        }
        mOnItemSelectedListener.add(listener);
    }

    public void removeOnItemSelectedListener(OnItemSelectedListener listener) {
        if (null == mOnItemSelectedListener) {
            return;
        }
        mOnItemSelectedListener.remove(listener);
    }

    /**
     * 获取被选中的position
     */
    public int getSelectedPosition() {
        if (getChildCount() <= 0) return RecyclerView.NO_POSITION;
        View centerView = getSelectedView();
        if (null == centerView) {
            return RecyclerView.NO_POSITION;
        }
        return getPosition(centerView);
    }

    protected void transformChildren() {
        if (!this.mIsScale && this.mAlpha == 1.f) {
            // 不需要变化
            return;
        }
        if (getChildCount() <= 0) {
            return;
        }

        View centerView = getSelectedView();
        if (null == centerView) {
            return;
        }
        int centerPosition = getPosition(centerView);

        if (getChildCount() <= 0) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int position = getPosition(child);
            if (position == centerPosition) {
                child.setScaleX(1f);
                child.setScaleY(1f);
                child.setAlpha(1.0f);
            } else {
                if (!this.mIsScale) {
                    final float scale = getScale(getIntervalCount(centerPosition, position));
                    child.setScaleX(scale);
                    child.setScaleY(scale);
                }
                if (this.mAlpha != 1.f) {
                    child.setAlpha(this.mAlpha);
                }
            }
        }
    }

    private float getScale(int intervalCount) {
        return 1.f - intervalCount * .1f;
    }

    /**
     * 获取两个position中间相差的item个数
     */
    private int getIntervalCount(int centerPosition, int position) {
        if (!mIsLoop) {
            return Math.abs(centerPosition - position);
        }

        // 例如：position=100,centerPosition=0这种情况
        if (position > centerPosition && position - centerPosition > mVisibleCount) {
            return getItemCount() - position;
        }

        // 例如：position=0,centerPosition=100这种情况
        if (position < centerPosition && centerPosition - position > mVisibleCount)
            return position + 1;

        return Math.abs(position - centerPosition);
    }

    /**
     * 分发OnItemFillListener事件
     */
    private void dispatchOnItemFillListener() {
        if (getChildCount() <= 0 || null == mOnItemFillListener || mOnItemFillListener.isEmpty()) {
            return;
        }

        View centerView = getSelectedView();
        if (null == centerView) {
            return;
        }
        int centerPosition = getPosition(centerView);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (null == child) {
                continue;
            }
            int position = getPosition(child);

            if (position == centerPosition) {
                onItemSelected(child, position);
            } else {
                onItemUnSelected(child, position);
            }
        }
    }

    protected void onItemSelected(View child, int position) {
        if (null == mOnItemFillListener) {
            return;
        }
        for (OnItemFillListener listener : mOnItemFillListener) {
            listener.onItemSelected(child, position);
        }
    }

    protected void onItemUnSelected(View child, int position) {
        if (null == mOnItemFillListener) {
            return;
        }
        for (OnItemFillListener listener : mOnItemFillListener) {
            listener.onItemUnSelected(child, position);
        }
    }

    public void addOnItemFillListener(OnItemFillListener listener) {
        if (null == mOnItemFillListener) {
            mOnItemFillListener = new ArrayList<>();
        }
        mOnItemFillListener.add(listener);
    }

    public void removeOnItemFillListener(OnItemFillListener listener) {
        if (null == mOnItemFillListener) {
            return;
        }
        mOnItemFillListener.remove(listener);
    }

    public int getOrientation() {
        return mOrientation;
    }

    /**
     * Builder模式
     */
    public static class Builder {
        private int orientation = VERTICAL;
        private int visibleCount = 5;
        private boolean isLoop = false;
        private boolean scale = true;
        private float alpha = 1.0f;

        public Builder setOrientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setVisibleCount(int visibleCount) {
            this.visibleCount = visibleCount;
            return this;
        }

        public Builder setIsLoop(boolean isLoop) {
            this.isLoop = isLoop;
            return this;
        }

        public Builder setScale(boolean scale) {
            this.scale = scale;
            return this;
        }

        public Builder setAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha) {
            this.alpha = alpha;
            return this;
        }

        public WheelLayoutManager build() {
            return new WheelLayoutManager(orientation, visibleCount, isLoop, scale, alpha);
        }
    }

}
