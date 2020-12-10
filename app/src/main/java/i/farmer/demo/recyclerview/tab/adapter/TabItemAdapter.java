
package i.farmer.demo.recyclerview.tab.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import i.farmer.demo.recyclerview.R;
import i.farmer.widget.recyclerview.tabs.TabViewAdapter;

/**
 * @author i-farmer
 * @created-time 2020/12/4 11:30 AM
 * @description
 */
public class TabItemAdapter extends TabViewAdapter<TabItemAdapter.TabItemHolder> {
    private LayoutInflater mLayoutInflater;
    private int indicatorPosition = 0;
    private List<String> mData;

    public TabItemAdapter(List<String> data) {
        this.mData = data;
    }

    @Override
    public void setCurrentIndicatorPosition(int indicatorPosition) {
        this.indicatorPosition = indicatorPosition;
    }

    @Override
    public int getIndicatorPosition() {
        return indicatorPosition;
    }

    @NonNull
    @Override
    public TabItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (null == mLayoutInflater) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        View itemView = mLayoutInflater.inflate(R.layout.item_tab, parent, false);
        return new TabItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TabItemHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    public class TabItemHolder extends RecyclerView.ViewHolder {
        public TabItemHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(String title) {
            ((TextView) itemView).setText(title);
        }
    }
}

