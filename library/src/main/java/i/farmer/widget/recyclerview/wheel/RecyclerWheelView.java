package i.farmer.widget.recyclerview.wheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import i.farmer.widget.recyclerview.R;

/**
 * @author i-farmer
 * @created-time 2021/1/15 11:12 上午
 * @description Wheel滚轮
 */
public class RecyclerWheelView extends RecyclerView {
    public RecyclerWheelView(@NonNull Context context) {
        this(context, null);
    }

    public RecyclerWheelView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerWheelView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = null;
        int orientation = VERTICAL;
        boolean isLoop = false;             // 是否循环
        int visibleCount = 3;               // 可见数量，必须是奇数
        float alpha = 1.f;                  // 未选中，显示透明度
        boolean scale = false;              // 未选中，是否缩放效果
        int highlightBackgroundColor = 0;   // 选中高亮背景色
        int highlightLineWidth = 1;         // 选中高亮 前后线条
        int highlightLineColor = 0;
        int highlightMarkerWidth = 0;       // 选中标签
        int highlightMarkerColor = 0;
        String hint = null;                 // 提示文案
        int hintSize = 16;
        int hintColor = 0XFF1A1A1A;
        int hintMarginEnd = 0;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecyclerWheelView);
            orientation = typedArray.getInt(R.styleable.RecyclerWheelView_android_orientation, orientation);
            alpha = typedArray.getFloat(R.styleable.RecyclerWheelView_android_unselectedAlpha, alpha);
            isLoop = typedArray.getBoolean(R.styleable.RecyclerWheelView_loop, false);
            visibleCount = typedArray.getInt(R.styleable.RecyclerWheelView_visibleCount, visibleCount);
            scale = typedArray.getInt(R.styleable.RecyclerWheelView_unselectedScaleMode, 1) == 2;
            highlightBackgroundColor = typedArray.getColor(R.styleable.RecyclerWheelView_highlightBackgroundColor, highlightBackgroundColor);
            highlightLineWidth = typedArray.getDimensionPixelOffset(R.styleable.RecyclerWheelView_highlightLineWidth, highlightLineWidth);
            highlightLineColor = typedArray.getColor(R.styleable.RecyclerWheelView_highlightLineColor, highlightLineColor);
            highlightMarkerWidth = typedArray.getDimensionPixelOffset(R.styleable.RecyclerWheelView_highlightMarkerWidth, highlightMarkerWidth);
            highlightMarkerColor = typedArray.getColor(R.styleable.RecyclerWheelView_highlightMarkerColor, highlightMarkerColor);
            hint = typedArray.getString(R.styleable.RecyclerWheelView_android_hint);
            hintSize = typedArray.getDimensionPixelSize(R.styleable.RecyclerWheelView_hintTextSize, hintSize);
            hintColor = typedArray.getColor(R.styleable.RecyclerWheelView_android_textColorHint, hintColor);
            hintMarginEnd = typedArray.getDimensionPixelOffset(R.styleable.RecyclerWheelView_hintMarginEnd, hintMarginEnd);
        } catch (Exception ex) {

        } finally {
            if (null != typedArray) {
                typedArray.recycle();
                typedArray = null;
            }
        }
        WheelLayoutManager manager = new WheelLayoutManager.Builder()
                .setAlpha(alpha)
                .setIsLoop(isLoop)
                .setVisibleCount(visibleCount)
                .setScale(scale)
                .setOrientation(orientation)
                .build();
        this.setLayoutManager(manager);

        WheelItemDecoration decoration = new WheelItemDecoration.Builder()
                .setHighlightBackground(highlightBackgroundColor)
                .setHighlightLine(highlightLineWidth, highlightLineColor)
                .setHighlightMarker(highlightMarkerWidth, highlightMarkerColor)
                .setHintText(hint, hintSize, hintColor, hintMarginEnd)
                .build();
        this.addItemDecoration(decoration);
    }

    @Override
    public void setLayoutManager(@Nullable RecyclerView.LayoutManager layout) {
        throw new IllegalArgumentException("Can not set LayoutManager here.");
    }

    @Nullable
    @Override
    public WheelLayoutManager getLayoutManager() {
        return (WheelLayoutManager) super.getLayoutManager();
    }

    public void addOnItemSelectedListener(WheelLayoutManager.OnItemSelectedListener listener) {
        getLayoutManager().addOnItemSelectedListener(listener);
    }

    public void removeOnItemSelectedListener(WheelLayoutManager.OnItemSelectedListener listener) {
        getLayoutManager().removeOnItemSelectedListener(listener);
    }

    public void addOnItemFillListener(WheelLayoutManager.OnItemFillListener listener) {
        getLayoutManager().addOnItemFillListener(listener);
    }

    public void removeOnItemFillListener(WheelLayoutManager.OnItemFillListener listener) {
        getLayoutManager().removeOnItemFillListener(listener);
    }

    public int getSelectedPosition() {
        return getLayoutManager().getSelectedPosition();
    }
}
