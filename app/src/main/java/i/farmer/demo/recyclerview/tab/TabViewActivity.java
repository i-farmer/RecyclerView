package i.farmer.demo.recyclerview.tab;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;

import i.farmer.demo.recyclerview.R;
import i.farmer.demo.recyclerview.tab.adapter.PageAdapter;
import i.farmer.demo.recyclerview.tab.adapter.TabItemAdapter;
import i.farmer.widget.recyclerview.tabs.RecyclerTabView;

/**
 * @author i-farmer
 * @created-time 2020/12/4 11:20 AM
 * @description
 */
public class TabViewActivity extends AppCompatActivity {
    private List<String> mData = Arrays.asList(
            "关注",
            "推荐",
            "热榜",
            "杭州",
            "精彩小说",
            "直播",
            "健康",
            "教育",
            "爱家居",
            "有驾行",
            "视频",
            "看国际",
            "游戏",
            "科技",
            "财经",
            "二次元动漫",
            "旅游",
            "情感",
            "搞笑",
            "图片"
    );
    private RecyclerTabView mTabView;
    private ViewPager2 mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_view);
        mTabView = findViewById(R.id.mTabView);
        mTabView.setAdapter(new TabItemAdapter(mData));

        mViewPager = findViewById(R.id.mViewPager);
        mViewPager.setAdapter(new PageAdapter(mData));

        mTabView.setUpWithViewPager2(mViewPager);

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 4) {
                    mViewPager.setBackgroundColor(Color.parseColor("#00ff00"));
                    mTabView.setBackgroundColor(Color.parseColor("#00ff00"));
                    mTabView.setIndicatorColor(Color.parseColor("#FFFFFF"));
                } else {
                    mViewPager.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mTabView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mTabView.setIndicatorColor(Color.parseColor("#1A1A1A"));
                }
            }
        });
    }
}
