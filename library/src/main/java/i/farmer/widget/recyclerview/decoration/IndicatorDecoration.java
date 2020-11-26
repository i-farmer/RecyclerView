package i.farmer.widget.recyclerview.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author i-farmer
 * @created-time 2020/11/25 12:02 PM
 * @description 指示器，支持三角形、圆形、圆角矩形，默认三角形
 * 建议配合 @link i.farmer.widget.recyclerview.manager.CenterLinearLayoutManager 使用
 */
public class IndicatorDecoration extends RecyclerView.ItemDecoration {
    public static final int SHAPE_TRIANGLE = 0;     // 三角形
    public static final int SHAPE_OVAL = 1;         // 圆形
    public static final int SHAPE_ROUND_RECT = 2;   // 圆角矩形

    private RecyclerView mTab;
    private ViewPager.OnPageChangeListener pageChangeCallback = null;
    private ViewPager2.OnPageChangeCallback pageChangeCallback2 = null;
    private Handler handler = null;

    private int indicatorWidth;                     // 指示器宽度
    private int indicatorHeight;                    // 指示器高度
    private int indicatorPadding;                   // 指示器跟item之间的间距
    private int itemSpacing;                        // item跟item之间的间距
    private int paddingStart;                       // item头部间距
    private int paddingEnd;                         // item尾部的间距

    private Paint indicatorPaint;
    private Path indicatorPath;

    private int oldPosition = 0;                    // 当前位置
    private int currentPosition = 0;                // 当前位置
    private float currentPositionOffset = 0f;       // 当前位置偏移量系数（0～1）

    private int staticShape;                        // 静止时候的形状
    private int scrollShape;                        // 滚动时候的形状

    @IntDef({SHAPE_TRIANGLE, SHAPE_OVAL, SHAPE_ROUND_RECT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ShapeMode {
    }

    private IndicatorDecoration(int color, int width, int height, int indicatorPadding,
                                int itemSpacing, int paddingStart, int paddingEnd,
                                int staticShape, int scrollShape) {
        this.indicatorWidth = width;
        this.indicatorHeight = height;
        this.indicatorPadding = indicatorPadding;
        this.itemSpacing = itemSpacing;
        this.paddingStart = paddingStart;
        this.paddingEnd = paddingEnd;
        this.staticShape = staticShape;
        this.scrollShape = scrollShape;

        this.indicatorPaint = new Paint();
        this.indicatorPaint.setColor(color);
        this.indicatorPaint.setAntiAlias(true);
        this.indicatorPaint.setStyle(Paint.Style.FILL);
        this.indicatorPath = new Path();
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        int activePosition = layoutManager.findFirstVisibleItemPosition();
        if (activePosition == RecyclerView.NO_POSITION) {
            // 没有数据
            return;
        }
        View oldView = layoutManager.findViewByPosition(this.oldPosition);
        if (null == oldView) {
            // 找不到
            return;
        }
        int position;
        if (this.currentPosition == this.oldPosition) {
            position = this.oldPosition + 1;
        } else {
            position = this.currentPosition;
        }
        View currentView = layoutManager.findViewByPosition(position);
        int centerNewView;
        if (null == currentView) {
            // 如果为空，就拿上一个item的宽度来计算
            if (this.currentPosition == this.oldPosition) {
                // 往右
                centerNewView = (int) (oldView.getRight() + itemSpacing + (oldView.getRight() - oldView.getLeft()) * 1.f / 2);
            } else {
                // 往左
                centerNewView = (int) (oldView.getLeft() - itemSpacing - (oldView.getRight() - oldView.getLeft()) * 1.f / 2);
            }
        } else {
            centerNewView = (int) ((currentView.getLeft() + currentView.getRight()) * 1.f / 2);
        }
        int centerOldView = (int) ((oldView.getLeft() + oldView.getRight()) * 1.f / 2);
        int distance = Math.abs(centerOldView - centerNewView);
        int start;
        if (this.oldPosition > this.currentPosition) {
            // 往左
            start = (int) (centerNewView + distance * 1.f * currentPositionOffset - indicatorWidth * 1.f / 2);
        } else {
            // 往右
            start = (int) (centerOldView + distance * 1.f * currentPositionOffset - indicatorWidth * 1.f / 2);
        }
        int shape = currentPositionOffset == 0 ? staticShape : scrollShape;
        // 绘制指示器
        this.indicatorPath.reset();
        int parentHeight = parent.getHeight();
        if (shape == SHAPE_OVAL) {
            // 圆
            this.indicatorPath.addCircle(start + indicatorWidth / 2,
                    parentHeight - indicatorHeight / 2,
                    indicatorHeight / 2,
                    Path.Direction.CW);
        } else if (shape == SHAPE_ROUND_RECT) {
            // 圆角矩形
            float radius = indicatorHeight / 2;
            this.indicatorPath.addRoundRect(start,
                    parentHeight - indicatorHeight,
                    start + indicatorWidth,
                    parentHeight,
                    radius, radius, Path.Direction.CW);
        } else {
            // 箭头
            this.indicatorPath.moveTo(start, parentHeight);
            this.indicatorPath.lineTo(start + indicatorWidth, parentHeight);
            this.indicatorPath.lineTo(start + indicatorWidth / 2, parentHeight - indicatorHeight);
            this.indicatorPath.close();
        }
        c.drawPath(this.indicatorPath, this.indicatorPaint);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int childPosition = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter().getItemCount();

        // 增加间隔
        outRect.left = (childPosition == 0 && paddingStart >= 0) ? paddingStart : itemSpacing;
        outRect.right = childPosition == itemCount - 1 ? (paddingEnd >= 0 ? paddingEnd : itemSpacing) : 0;
        // 增加指示器高度，以及间隔
        outRect.bottom = indicatorHeight + indicatorPadding;
    }

    /**
     * @param tab
     * @param viewPager 跟ViewPager配合使用
     * @see androidx.viewpager.widget.ViewPager
     */
    private void attach(RecyclerView tab, ViewPager viewPager) {
        if (null == tab || null == tab.getAdapter()) {
            throw new IllegalArgumentException("RecyclerView or RecyclerView adapter can not be NULL !!");
        }
        if (null == viewPager || null == viewPager.getAdapter()) {
            throw new IllegalArgumentException("ViewPager2 or ViewPager2 adapter can not be NULL !!");
        }
        if (tab.getAdapter().getItemCount() != viewPager.getAdapter().getCount()) {
            throw new IllegalArgumentException("Tab length must be the same as the page count !");
        }
        this.mTab = tab;
        tab.addItemDecoration(this);
        if (null == pageChangeCallback) {
            pageChangeCallback = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    IndicatorDecoration.this.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

                @Override
                public void onPageSelected(int position) {
                    IndicatorDecoration.this.onPageSelected(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    // NO-OP
                }
            };
        }
        viewPager.removeOnPageChangeListener(pageChangeCallback);
        viewPager.addOnPageChangeListener(pageChangeCallback);
    }

    /**
     * @param tab
     * @param viewPager 跟ViewPager2配合使用
     * @see androidx.viewpager2.widget.ViewPager2
     */
    private void attach(RecyclerView tab, ViewPager2 viewPager) {
        if (null == tab || null == tab.getAdapter()) {
            throw new IllegalArgumentException("RecyclerView or RecyclerView adapter can not be NULL !!");
        }
        if (null == viewPager || null == viewPager.getAdapter()) {
            throw new IllegalArgumentException("ViewPager2 or ViewPager2 adapter can not be NULL !!");
        }
        if (tab.getAdapter().getItemCount() != viewPager.getAdapter().getItemCount()) {
            throw new IllegalArgumentException("Tab length must be the same as the page count !");
        }
        this.mTab = tab;
        tab.addItemDecoration(this);
        if (null == pageChangeCallback2) {
            pageChangeCallback2 = new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    IndicatorDecoration.this.onPageSelected(position);
                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    IndicatorDecoration.this.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            };
        }
        viewPager.unregisterOnPageChangeCallback(pageChangeCallback2);
        viewPager.registerOnPageChangeCallback(pageChangeCallback2);
    }

    private void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.currentPosition = position;
        this.currentPositionOffset = positionOffset;
        this.mTab.invalidate();
    }

    private void onPageSelected(int position) {
        this.oldPosition = position;
        getHandler().sendEmptyMessageDelayed(77, 200);
    }

    private Handler getHandler() {
        if (null == handler) {
            handler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    IndicatorDecoration.this.mTab.smoothScrollToPosition(oldPosition);
                }
            };
        } else {
            handler.removeMessages(77);
        }
        return handler;
    }

    public static class Builder {
        private int indicatorColor = 0XFF1A1A1A;        // 指示器颜色
        private int indicatorWidth = 18;                // 指示器宽度
        private int indicatorHeight = 0;                // 指示器高度
        private int indicatorPadding = 10;              // 指示器跟item之间的间距
        private int itemSpacing = 0;                    // item跟item之间的间距
        private int paddingStart = -1;                  // item头部间距
        private int paddingEnd = -1;                    // item尾部的间距
        private Context context;
        private int staticShape = SHAPE_TRIANGLE;       // 静止时候的形状
        private int scrollShape = SHAPE_TRIANGLE;       // 滚动时候的形状


        private void checkContext() {
            if (null == context) {
                throw new IllegalArgumentException("Context can not be null!");
            }
        }

        public Builder() {
        }

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setShape(@ShapeMode int shape) {
            return setShape(shape, shape);
        }

        public Builder setShape(@ShapeMode int staticShape, @ShapeMode int scrollShape) {
            this.staticShape = staticShape;
            this.scrollShape = scrollShape;
            return this;
        }

        public Builder setColor(int color) {
            this.indicatorColor = color;
            return this;
        }

        public Builder setColorRes(@ColorRes int color) {
            checkContext();
            int c = context.getResources().getColor(color);
            return setColor(c);
        }

        public Builder setSize(int width) {
            return setSize(width, width / 2);
        }

        public Builder setSize(int width, int height) {
            this.indicatorWidth = width;
            this.indicatorHeight = height;
            return this;
        }

        public Builder setSizeRes(@DimenRes int width) {
            checkContext();
            int w = context.getResources().getDimensionPixelOffset(width);
            return setSize(w);
        }

        public Builder setSizeRes(@DimenRes int width, @DimenRes int height) {
            checkContext();
            int w = context.getResources().getDimensionPixelOffset(width);
            int h = context.getResources().getDimensionPixelOffset(height);
            return setSize(w, h);
        }

        public Builder setIndicatorPadding(int padding) {
            this.indicatorPadding = padding;
            return this;
        }

        public Builder setIndicatorPaddingRes(@DimenRes int padding) {
            checkContext();
            int p = context.getResources().getDimensionPixelOffset(padding);
            return setIndicatorPadding(p);
        }

        public Builder setItemSpacing(int itemSpacing) {
            this.itemSpacing = itemSpacing;
            return this;
        }

        public Builder setItemSpacingRes(@DimenRes int itemSpacing) {
            checkContext();
            int s = context.getResources().getDimensionPixelOffset(itemSpacing);
            return setItemSpacing(s);
        }

        public Builder setPadding(int padding) {
            this.paddingStart = this.paddingEnd = padding;
            return this;
        }

        public Builder setPaddingRes(@DimenRes int padding) {
            checkContext();
            int p = context.getResources().getDimensionPixelOffset(padding);
            return setPadding(p);
        }

        public Builder setPaddingStart(int start) {
            this.paddingStart = start;
            return this;
        }

        public Builder setPaddingStartRes(@DimenRes int start) {
            checkContext();
            int p = context.getResources().getDimensionPixelOffset(start);
            return setPaddingStart(p);
        }

        public Builder setPaddingEnd(int end) {
            this.paddingEnd = end;
            return this;
        }

        public Builder setPaddingEndRes(@DimenRes int end) {
            checkContext();
            int p = context.getResources().getDimensionPixelOffset(end);
            return setPaddingEnd(p);
        }

        private IndicatorDecoration build() {
            if (this.indicatorHeight <= 0) {
                this.indicatorHeight = this.indicatorWidth / 2;
            }
            return new IndicatorDecoration(
                    this.indicatorColor,
                    this.indicatorWidth,
                    this.indicatorHeight,
                    this.indicatorPadding,
                    this.itemSpacing,
                    this.paddingStart,
                    this.paddingEnd,
                    this.staticShape,
                    this.scrollShape
            );
        }

        public void attach(RecyclerView tab, ViewPager viewPager) {
            build().attach(tab, viewPager);
        }

        public void attach(RecyclerView tab, ViewPager2 viewPager) {
            build().attach(tab, viewPager);
        }
    }
}
