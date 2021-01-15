package i.farmer.widget.recyclerview.wheel.adapter;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import i.farmer.widget.recyclerview.wheel.model.IWheel;

/**
 * @author i-farmer
 * @created-time 2021/1/15 3:33 下午
 * @description
 */
public abstract class WheelAdapter<T extends IWheel>
        extends RecyclerView.Adapter<WheelAdapter.WheelViewHolder> {
    private List<T> mData;
    private int selectedPosition = -1;

    public WheelAdapter() {
    }

    public WheelAdapter(List<T> data) {
        this.mData = data;
    }

    public void setData(List<T> data) {
        this.mData = mData;
        this.notifyDataSetChanged();
    }

    public void setSelected(int position) {
        if (this.selectedPosition == position) {
            return;
        }
        final int itemCount = getItemCount();
        if (position < 0 || position >= itemCount) {
            return;
        }
        int old = this.selectedPosition;
        this.selectedPosition = position;
        notifyItemChanged(old);
        notifyItemChanged(position);
    }

    public void detachSelected() {
        if (this.selectedPosition == -1) {
            return;
        }
        int old = this.selectedPosition;
        this.selectedPosition = -1;
        notifyItemChanged(old);
    }

    public T getValue(int position) {
        if (null == mData) {
            return null;
        }
        if (position < 0 || position >= mData.size()) {
            return null;
        }
        return mData.get(position);
    }

    public int findPositionOf(T value) {
        return findPositionOf(value.getLabel());
    }

    public int findPositionOf(String value) {
        if (TextUtils.isEmpty(value)) {
            return -1;
        }
        if (null == mData) {
            return -1;
        }
        for (int i = 0; i < mData.size(); i++) {
            T v = mData.get(i);
            if (v.getLabel().equals(value)) {
                return i;
            }
        }
        return -1;
    }

    protected RecyclerView.LayoutParams getDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    protected abstract int getTextColor(boolean selected);

    protected abstract int getTextSize();

    @NonNull
    @Override
    public WheelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(getDefaultLayoutParams());
        return new WheelViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull WheelAdapter.WheelViewHolder holder, int position) {
        holder.bind(mData.get(position), position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    class WheelViewHolder extends RecyclerView.ViewHolder {
        public WheelViewHolder(@NonNull TextView itemView) {
            super(itemView);
        }

        public void bind(IWheel item, boolean selected) {
            ((TextView) itemView).setText(item.getLabel());
            ((TextView) itemView).setTextColor(getTextColor(selected));
            ((TextView) itemView).setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize());
        }
    }
}
