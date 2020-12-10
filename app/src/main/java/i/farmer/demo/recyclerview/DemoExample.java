package i.farmer.demo.recyclerview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.Arrays;
import java.util.List;

import i.farmer.demo.recyclerview.tab.TabViewActivity;
import i.farmer.demo.recyclerview.utils.ButtonClickUtil;
import i.farmer.demo.recyclerview.utils.ColorUtil;
import i.farmer.demo.recyclerview.utils.JumpUtil;
import i.farmer.widget.recyclerview.decoration.GridItemDecoration;

/**
 * 例子列表
 */
public class DemoExample extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        RecyclerView mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(new DemoListAdapter());
        mRecyclerView.addItemDecoration(new GridItemDecoration(20));
    }

    class DemoListAdapter extends RecyclerView.Adapter<DemoListAdapter.DemoListViewHolder> {
        private List<DemoItem> mData = Arrays.asList(
                new DemoItem("流布局", "FlowLayout", TabViewActivity.class)
        );

        private LayoutInflater mLayoutInflater;

        @NonNull
        @Override
        public DemoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (null == mLayoutInflater) {
                mLayoutInflater = LayoutInflater.from(parent.getContext());
            }
            View itemView = mLayoutInflater.inflate(R.layout.item_demo_list, parent, false);
            return new DemoListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DemoListViewHolder holder, int position) {
            holder.bind(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class DemoListViewHolder extends RecyclerView.ViewHolder {
            private TextView mTitleTv;
            private TextView mSubTitleTv;
            private Class<? extends Activity> clz;

            public DemoListViewHolder(@NonNull View itemView) {
                super(itemView);
                mTitleTv = itemView.findViewById(R.id.mTitleTv);
                mSubTitleTv = itemView.findViewById(R.id.mSubTitleTv);
                ButtonClickUtil.setAlphaChange(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JumpUtil.enter(itemView.getContext(), clz);
                    }
                });
            }

            public void bind(DemoItem item) {
                this.clz = item.getClz();
                mTitleTv.setText(item.getTitle());
                mSubTitleTv.setText(item.getSubTitle());
                itemView.setBackgroundColor(item.getBgColor());
                if (ColorUtil.isLightColor(item.getBgColor())) {
                    mTitleTv.setTextColor(Color.parseColor("#1A1A1A"));
                    mSubTitleTv.setTextColor(Color.parseColor("#1A1A1A"));
                } else {
                    mTitleTv.setTextColor(Color.parseColor("#FFFFFF"));
                    mSubTitleTv.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }
        }
    }

    class DemoItem {
        private String title;
        private String subTitle;
        private Class<? extends Activity> clz;
        private int bgColor;

        public DemoItem(String title, String subTitle, Class<? extends Activity> clz) {
            this.title = title;
            this.subTitle = subTitle;
            this.clz = clz;
            this.bgColor = ColorUtil.randomColor();
        }

        public String getTitle() {
            return title;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public Class<? extends Activity> getClz() {
            return clz;
        }

        public int getBgColor() {
            return bgColor;
        }
    }

}
