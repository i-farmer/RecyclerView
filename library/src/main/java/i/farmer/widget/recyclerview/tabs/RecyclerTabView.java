package i.farmer.widget.recyclerview.tabs;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import i.farmer.widget.recyclerview.R;
import i.farmer.widget.recyclerview.decoration.LinearSpacingDecoration;
import i.farmer.widget.recyclerview.manager.CenterLinearLayoutManager;

/**
 * @author i-farmer
 * @created-time 2020/12/8 10:41 AM
 * @description 基于RecyclerView实现的TabLayout，同时支持横向、竖向
 */
public class RecyclerTabView extends RecyclerView implements RecyclerTabViewAdapter.OnRecyclerTabViewClickListener {
    public interface OnRecyclerTabViewCallback {
        void onTabItemChanged(int position);

        void onTabItemReClick(int position);
    }

    private final int INDICATOR_STYLE_LINE = 1;                     // 线条指示器
    private final int INDICATOR_STYLE_FULL_LINE = 2;                // 同tabItem等宽线条指示器
    private final int INDICATOR_STYLE_TRIANGLE = 3;                 // 三角指示器
    private final int INDICATOR_STYLE_BLOCK = 4;                    // 块状背景指示器

    protected boolean mScrollEnabled = true;                        // tab是否可以滚动
    private int mOrientation = HORIZONTAL;
    private LinearLayoutManager mLayoutManager;                     // layoutManager

    private ViewPager mViewPager = null;                            // 关联的ViewPager，只允许关联一个
    private ViewPager2 mViewPager2 = null;

    private OnRecyclerTabViewCallback callback;                     // 回掉

    public RecyclerTabView(@NonNull Context context) {
        this(context, null);
    }

    public RecyclerTabView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerTabView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOverScrollMode(View.OVER_SCROLL_NEVER); // 不要over效果，并且通过父类setWillNotDraw(false)设置 需要调用onDraw()进行绘制
        // 解析属性
        int indicatorStyle = INDICATOR_STYLE_LINE;  // 指示器类型
        int indicatorColor = 0XFF1A1A1A;            // 指示器颜色
        int indicatorWidth = 0;
        int indicatorHeight = 0;
        boolean indicatorSmoothCircle = false;      // 在滑动过程中，是否绘制小圆点指示器，目前只有在三角形指示器中可用
        int tabItemSpacing = 0;                     // 每个item之间的间距
        int tabPaddingStart = 0;                    // 整个tab的paddingStart
        int tabPaddingEnd = 0;                      // 整个tab的paddingEnd
        int indicatorPadding = 0;                   // fullLine、block指示器，左右两边间距，不能大于itemSpacing，不指定的话等于itemSpacing的一半
        boolean includeGap = true;                  // 指示器滑动过程中是否包含gap差值计算
        if (null != attrs) {
            TypedArray typedArray = null;
            try {
                typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecyclerTabView,
                        defStyleAttr, R.style.RecyclerTabView_Default);
                mOrientation = typedArray.getInt(R.styleable.RecyclerTabView_android_orientation, mOrientation);
                indicatorStyle = typedArray.getInt(R.styleable.RecyclerTabView_indicatorStyle, indicatorStyle);
                indicatorColor = typedArray.getColor(R.styleable.RecyclerTabView_indicatorColor, indicatorColor);
                indicatorWidth = typedArray.getDimensionPixelOffset(R.styleable.RecyclerTabView_indicatorWidth, indicatorWidth);
                indicatorHeight = typedArray.getDimensionPixelOffset(R.styleable.RecyclerTabView_indicatorHeight, indicatorHeight);
                indicatorSmoothCircle = typedArray.getBoolean(R.styleable.RecyclerTabView_indicatorSmoothCircle, indicatorSmoothCircle);
                tabItemSpacing = typedArray.getDimensionPixelOffset(R.styleable.RecyclerTabView_tabItemSpacing, tabItemSpacing);
                indicatorPadding = typedArray.getDimensionPixelOffset(R.styleable.RecyclerTabView_indicatorPadding, indicatorPadding);
                int tabPadding = typedArray.getDimensionPixelOffset(R.styleable.RecyclerTabView_tabPadding, 0);
                tabPaddingStart = typedArray.getDimensionPixelOffset(R.styleable.RecyclerTabView_tabPaddingStart, tabPadding);
                tabPaddingEnd = typedArray.getDimensionPixelOffset(R.styleable.RecyclerTabView_tabPaddingEnd, tabPadding);
                includeGap = typedArray.getInt(R.styleable.RecyclerTabView_indicatorGap, 1) == 1;

            } catch (Exception ex) {

            } finally {
                if (null != typedArray) {
                    typedArray.recycle();
                    typedArray = null;
                }
            }
        }
        // 设置布局管理器
        mLayoutManager = new CenterLinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollHorizontally() {
                return mOrientation == HORIZONTAL ? mScrollEnabled : false;
            }

            @Override
            public boolean canScrollVertically() {
                return mOrientation == VERTICAL ? mScrollEnabled : false;
            }
        };
        mLayoutManager.setOrientation(mOrientation);
        setLayoutManager(mLayoutManager);
        // 增加指示器
        if (indicatorPadding < 0) {
            // 默认不增加
            indicatorPadding = 0;
        } else if (indicatorPadding > tabItemSpacing) {
            // 不能超出间距
            indicatorPadding = tabItemSpacing;
        }
        if (indicatorStyle == INDICATOR_STYLE_LINE) {
            addItemDecoration(new LineIndicator(includeGap, indicatorColor, indicatorWidth, indicatorHeight, tabItemSpacing));
        } else if (indicatorStyle == INDICATOR_STYLE_FULL_LINE) {
            if (indicatorPadding <= 0) {
                indicatorPadding = tabItemSpacing / 2;
            } else if (indicatorPadding > tabItemSpacing) {
                indicatorPadding = tabItemSpacing;
            }
            addItemDecoration(new FullLineIndicator(includeGap, indicatorPadding, indicatorColor, indicatorWidth, indicatorHeight, tabItemSpacing));
        } else if (indicatorStyle == INDICATOR_STYLE_TRIANGLE) {
            addItemDecoration(new TriangleIndicator(indicatorSmoothCircle, indicatorColor, indicatorWidth, indicatorHeight, tabItemSpacing));
        } else if (indicatorStyle == INDICATOR_STYLE_BLOCK) {
            addItemDecoration(new BlockIndicator(includeGap, indicatorColor, indicatorPadding, tabItemSpacing));
        }
        // 增加间距
        if (tabItemSpacing > 0 || tabPaddingStart > 0 || tabPaddingEnd > 0) {
            if (mLayoutManager.getOrientation() == HORIZONTAL) {
                addItemDecoration(new LinearSpacingDecoration(tabItemSpacing, tabPaddingStart, 0, tabPaddingEnd, 0));
            } else {
                addItemDecoration(new LinearSpacingDecoration(tabItemSpacing, 0, tabPaddingStart, 0, tabPaddingEnd));
            }
        }
        // 不需要动画
        setItemAnimator(null);
    }

    /**
     * 滚动指示器 增加偏移量
     *
     * @param position
     * @param positionOffset
     */
    private void scrollToTabIndicator(int position, float positionOffset) {
        final int count = getItemDecorationCount();
        for (int i = 0; i < count; i++) {
            ItemDecoration decoration = getItemDecorationAt(i);
            if (decoration instanceof RecyclerTabViewIndicator) {
                ((RecyclerTabViewIndicator) decoration).scrollToTabIndicator(position, positionOffset);
            }
        }
        invalidateItemDecorations();
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        if (null == adapter) {
            throw new IllegalArgumentException("The Adapter can not be in null.");
        }
        if (!(adapter instanceof RecyclerTabViewAdapter)) {
            throw new IllegalArgumentException("This Adapter is not supported.");
        }
        super.setAdapter(adapter);
    }

    /**
     * 关联ViewPager
     *
     * @param viewPager
     * @see ViewPager
     */
    public void setUpWithViewPager(ViewPager viewPager) {
        if (null == viewPager || null == viewPager.getAdapter()) {
            throw new IllegalArgumentException("ViewPager or ViewPager adapter can not be NULL!");
        }
        if (null == getAdapter()) {
            throw new IllegalArgumentException("Please set RecyclerTabView adapter first.");
        }
        if (viewPager.getAdapter().getCount() != getAdapter().getItemCount()) {
            throw new IllegalArgumentException("Tab item length must be the same as the page count!");
        }
        clearAttach();
        this.mViewPager = viewPager;
        this.mViewPager.addOnPageChangeListener(getOnPageChangeCallback());
        ((RecyclerTabViewAdapter) getAdapter()).setClickListener(this);
    }

    /**
     * 关联ViewPager2
     *
     * @param viewPager
     * @see ViewPager2
     */
    public void setUpWithViewPager2(ViewPager2 viewPager) {
        if (null == viewPager || null == viewPager.getAdapter()) {
            throw new IllegalArgumentException("ViewPager or ViewPager adapter can not be NULL!");
        }
        if (null == getAdapter()) {
            throw new IllegalArgumentException("Please set RecyclerTabView adapter first.");
        }
        if (viewPager.getAdapter().getItemCount() != getAdapter().getItemCount()) {
            throw new IllegalArgumentException("Tab item length must be the same as the page count!");
        }
        clearAttach();
        this.mViewPager2 = viewPager;
        this.mViewPager2.registerOnPageChangeCallback(getOnPageChangeCallback());
        ((RecyclerTabViewAdapter) getAdapter()).setClickListener(this);
    }

    /**
     * 清除关联
     */
    private void clearAttach() {
        if (null != this.mViewPager) {
            if (null != onPageChangeCallback) {
                this.mViewPager.removeOnPageChangeListener(onPageChangeCallback);
            }
            this.mViewPager = null;
        }
        if (null != this.mViewPager2) {
            if (null != onPageChangeCallback) {
                this.mViewPager2.unregisterOnPageChangeCallback(onPageChangeCallback);
            }
            this.mViewPager2 = null;
        }
    }

    private ViewPagerOnPageChangeCallback onPageChangeCallback;     // ViewPager、ViewPager2的监听

    private ViewPagerOnPageChangeCallback getOnPageChangeCallback() {
        if (null == onPageChangeCallback) {
            onPageChangeCallback = new ViewPagerOnPageChangeCallback();
        }
        return onPageChangeCallback;
    }

    class ViewPagerOnPageChangeCallback extends ViewPager2.OnPageChangeCallback implements ViewPager.OnPageChangeListener {

        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            RecyclerTabView.this.scrollToTabIndicator(position, positionOffset);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
        }

        @Override
        public void onPageSelected(int position) {
            RecyclerTabViewAdapter adapter = (RecyclerTabViewAdapter) RecyclerTabView.this.getAdapter();
            if (adapter.getIndicatorPosition() == position) {
                return;
            }
            if (null != callback) {
                callback.onTabItemChanged(position);                        // 回掉
            }
            adapter.setIndicatorPosition(position);                         // 刷新指示器
            if (mScrollState == ViewPager2.SCROLL_STATE_IDLE) {
                RecyclerTabView.this.scrollToTabIndicator(position, 0);     // 绘制指示器
            }
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    smoothScrollToPosition(position);                       // 滚动到目标
                }
            }, 200);
        }
    }

    /**
     * 设置是否可以滚动
     *
     * @param enabled
     */
    public void setScrollEnabled(boolean enabled) {
        this.mScrollEnabled = enabled;
    }

    /**
     * 适配器点击事件（直接选中）
     *
     * @param position
     * @param smoothScroll 是否顺滑
     */
    @Override
    public void setCurrentItem(int position, boolean smoothScroll) {
        if (null != callback
                && ((RecyclerTabViewAdapter) getAdapter()).getIndicatorPosition() == position) {
            // 重复点击
            callback.onTabItemReClick(position);
        }
        if (null != mViewPager) {
            mViewPager.setCurrentItem(position, smoothScroll);
        } else if (null != mViewPager2) {
            mViewPager2.setCurrentItem(position, smoothScroll);
        }
    }

    /**
     * 设置指示器颜色
     *
     * @param color
     */
    public void setIndicatorColor(@ColorInt int color) {
        final int count = getItemDecorationCount();
        for (int i = 0; i < count; i++) {
            ItemDecoration decoration = getItemDecorationAt(i);
            if (decoration instanceof RecyclerTabViewIndicator) {
                ((RecyclerTabViewIndicator) decoration).setIndicatorColor(color);
            }
        }
    }

    /**
     * 设置回掉
     *
     * @param callback
     */
    public void setCallback(OnRecyclerTabViewCallback callback) {
        this.callback = callback;
    }
}
