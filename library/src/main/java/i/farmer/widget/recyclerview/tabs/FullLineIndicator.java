package i.farmer.widget.recyclerview.tabs;

/**
 * @author i-farmer
 * @created-time 2020/12/8 11:47 AM
 * @description 同tabItem等宽线条指示器
 */
class FullLineIndicator extends LineIndicator {
    private boolean includeSpacing;

    public FullLineIndicator(boolean includeGap, boolean includeSpacing,
                             int color, int width, int height, int spacing) {
        super(includeGap, color, width, height, spacing);
        this.includeSpacing = includeSpacing;
    }

    @Override
    protected float getIndicatorSize(boolean horizontal, float start, float end) {
        return end - start + (includeSpacing ? mItemSpacing : 0);
    }

    @Override
    protected float getIndicatorGap(float size, float nextSize, float distance, float positionOffset) {
        return (nextSize - size) * positionOffset;
    }
}
