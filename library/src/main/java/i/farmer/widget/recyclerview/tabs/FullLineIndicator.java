package i.farmer.widget.recyclerview.tabs;

/**
 * @author i-farmer
 * @created-time 2020/12/8 11:47 AM
 * @description 同tabItem等宽线条指示器
 */
class FullLineIndicator extends LineIndicator {
    private int indicatorPadding;

    public FullLineIndicator(boolean includeGap, int indicatorPadding,
                             int color, int width, int height, int spacing) {
        super(includeGap, color, width, height,
                spacing, RecyclerTabView.INDICATOR_GRAVITY_CENTER, 0);
        this.indicatorPadding = indicatorPadding;
    }

    @Override
    protected float getIndicatorSize(boolean horizontal, float start, float end) {
        return end - start + indicatorPadding * 2;
    }

    @Override
    protected float getIndicatorGap(float size, float nextSize, float distance, float positionOffset) {
        return (nextSize - size) * positionOffset;
    }
}
