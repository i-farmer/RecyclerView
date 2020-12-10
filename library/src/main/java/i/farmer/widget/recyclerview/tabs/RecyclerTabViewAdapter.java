package i.farmer.widget.recyclerview.tabs;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author i-farmer
 * @created-time 2020/12/8 2:16 PM
 * @description RecyclerTabView 适配器的基类
 */
public abstract class RecyclerTabViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    /**
     * 设置当前选中指示器下标
     *
     * @param indicatorPosition
     */
    public abstract void setCurrentIndicatorPosition(int indicatorPosition);

    /**
     * 获得当前选中指示器下标
     *
     * @return
     */
    public abstract int getIndicatorPosition();
}
