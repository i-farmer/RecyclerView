package i.farmer.demo.recyclerview.tab.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import i.farmer.demo.recyclerview.R;
import i.farmer.demo.recyclerview.utils.ButtonClickUtil;
import i.farmer.widget.recyclerview.tabs.RecyclerTabViewAdapter;

/**
 * @author i-farmer
 * @created-time 2020/12/4 11:30 AM
 * @description
 */
public class TabItemAdapter extends RecyclerTabViewAdapter<String> {

    public TabItemAdapter(List<String> data) {
        super(R.layout.item_tab, data);
    }

    @Override
    protected void onBindData(@NonNull RecyclerTabViewHolder holder, String data, boolean selected, int position) {
        TextView view = (TextView) holder.itemView;
        view.setText(data);
        view.setTextColor(Color.parseColor(selected ? "#1A1A1A" : "#999999"));
        ButtonClickUtil.setAlphaChange(view);
        view.setClickable(selected);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.setCurrentItem(position);
            }
        });
    }

}

