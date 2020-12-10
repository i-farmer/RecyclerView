package i.farmer.demo.recyclerview.tab.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import i.farmer.demo.recyclerview.R;

/**
 * @author i-farmer
 * @created-time 2020/12/4 11:30 AM
 * @description
 */
public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<String> mData;

    public PageAdapter(List<String> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (null == mLayoutInflater) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        View itemView = mLayoutInflater.inflate(R.layout.item_page, parent, false);
        return new PageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class PageViewHolder extends RecyclerView.ViewHolder {
        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(String title) {
            ((TextView) itemView).setText(title);
        }
    }
}