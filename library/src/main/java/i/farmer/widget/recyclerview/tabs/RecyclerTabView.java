package i.farmer.widget.recyclerview.tabs;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import i.farmer.widget.recyclerview.R;
import i.farmer.widget.recyclerview.manager.CenterLinearLayoutManager;

/**
 * @author i-farmer
 * @created-time 2020/12/8 10:41 AM
 * @description 基于RecyclerView实现的TabLayout，同时支持横向、竖向
 */
public class RecyclerTabView extends RecyclerView {
    protected boolean mScrollEnabled = true;                        // tab是否可以滚动
    private int mOrientation = HORIZONTAL;
    private LinearLayoutManager mLayoutManager;                     // layoutManager

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
        resolveAttrs(context, attrs, defStyleAttr);
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
        // 不需要动画
        setItemAnimator(null);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        if (null == adapter) {
            throw new IllegalArgumentException("The Adapter can not be in null.");
        }
        if (!(adapter instanceof TabViewAdapter)) {
            throw new IllegalArgumentException("This Adapter is not supported.");
        }
        super.setAdapter(adapter);
    }

    /**
     * 解析属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void resolveAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        if (null == attrs) {
            return;
        }
        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecyclerTabView,
                    defStyleAttr, R.style.RecyclerTabView_Default);
            mOrientation = typedArray.getInt(R.styleable.RecyclerTabView_android_orientation, mOrientation);
        } catch (Exception ex) {

        } finally {
            if (null != typedArray) {
                typedArray.recycle();
                typedArray = null;
            }
        }
    }

    /**
     * 直接选中
     *
     * @param position
     */
    public void setCurrentItem(int position) {
        scrollToTabIndicator(position, 0);

        ((TabViewAdapter) getAdapter()).setCurrentIndicatorPosition(position);      // 选中
        getAdapter().notifyDataSetChanged();
    }

    /**
     * 滚动指示器 增加偏移量
     *
     * @param position
     * @param positionOffset
     */
    public void scrollToTabIndicator(int position, float positionOffset) {
        final int count = getItemDecorationCount();
        for (int i = 0; i < count; i++) {
            ItemDecoration decoration = getItemDecorationAt(i);
            if (decoration instanceof RecyclerTabIndicator) {
                ((RecyclerTabIndicator) decoration).scrollToTabIndicator(position, positionOffset);
            }
        }
        invalidateItemDecorations();
    }

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
        viewPager.addOnPageChangeListener(new ViewPagerOnPageChangeListener());
    }

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
        viewPager.registerOnPageChangeCallback(new ViewPager2OnPageChangeCallback());
    }

    class ViewPager2OnPageChangeCallback extends ViewPager2.OnPageChangeCallback {

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
            RecyclerTabView.this.onPageSelected(mScrollState == ViewPager2.SCROLL_STATE_IDLE, position);
        }
    }

    class ViewPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {

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
            RecyclerTabView.this.onPageSelected(mScrollState == ViewPager.SCROLL_STATE_IDLE, position);
        }
    }

    /**
     * 当监听ViewPager、ViewPager2选中page的时候
     *
     * @param IDLE
     * @param position
     */
    private void onPageSelected(boolean IDLE, int position) {
        TabViewAdapter adapter = (TabViewAdapter) RecyclerTabView.this.getAdapter();
        if (adapter.getIndicatorPosition() != position) {
            adapter.setCurrentIndicatorPosition(position);
            this.smoothScrollToPosition(position);  // 滚动到目标
            if (IDLE) {
                this.scrollToTabIndicator(position, 0);  // 绘制指示器
            }
        }
    }
}
