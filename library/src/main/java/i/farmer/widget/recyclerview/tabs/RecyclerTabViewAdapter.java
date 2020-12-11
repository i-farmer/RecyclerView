package i.farmer.widget.recyclerview.tabs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @author i-farmer
 * @created-time 2020/12/8 2:16 PM
 * @description RecyclerTabView 适配器的基类
 */
public abstract class RecyclerTabViewAdapter<T> extends RecyclerView.Adapter<RecyclerTabViewAdapter.RecyclerTabViewHolder> {
    public interface OnRecyclerTabViewClickListener {
        void setCurrentItem(int position, boolean smoothScroll);
    }

    protected class RecyclerTabViewHolder extends RecyclerView.ViewHolder {
        public RecyclerTabViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public <T extends View> T $View(@IdRes int id) {
            return itemView.findViewById(id);
        }
    }

    private @LayoutRes
    int layoutItemRes;
    protected LayoutInflater mLayoutInflater;
    protected List<T> mData;
    protected int indicatorPosition = 0;                        // 选中指示器
    protected OnRecyclerTabViewClickListener clickListener;     // 点击事件

    public RecyclerTabViewAdapter(int layoutItemRes, List<T> data) {
        this.layoutItemRes = layoutItemRes;
        this.mData = data;
    }

    @NonNull
    @Override
    public RecyclerTabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (null == mLayoutInflater) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        View itemView = mLayoutInflater.inflate(layoutItemRes, parent, false);
        return new RecyclerTabViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerTabViewAdapter.RecyclerTabViewHolder holder, int position) {
        onBindData(holder, mData.get(position), indicatorPosition == position, position);
    }

    /**
     * View绑定数据
     *
     * @param holder
     * @param data
     * @param selected 是否选中
     * @param position
     */
    protected abstract void onBindData(@NonNull RecyclerTabViewHolder holder, T data, boolean selected, int position);

    /**
     * 点击事件
     *
     * @param listener
     */
    public void setClickListener(OnRecyclerTabViewClickListener listener) {
        this.clickListener = listener;
    }

    /**
     * 设置当前选中指示器下标
     *
     * @param position
     */
    public void setIndicatorPosition(int position) {
        this.indicatorPosition = position;
        this.notifyDataSetChanged();    // 刷新
    }

    /**
     * 获得当前选中指示器下标
     *
     * @return
     */
    public int getIndicatorPosition() {
        return this.indicatorPosition;
    }
}
