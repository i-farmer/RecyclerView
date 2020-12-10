package i.farmer.widget.recyclerview.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * 给RecyclerView的GridLayoutManager布局器增加分割线
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {
    protected int mSpace;                       // 间隔大小
    private boolean mIncludeEdge = true;        // 是否包括边缘

    public GridItemDecoration(int space) {
        this.mSpace = space;
    }

    public GridItemDecoration(int space, boolean includeEdge) {
        this.mSpace = space;
        this.mIncludeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int spanCount;   // 列数
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            spanCount = layoutManager.getSpanCount();
        } else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) parent.getLayoutManager();
            spanCount = layoutManager.getSpanCount();
        } else {
            throw new RuntimeException("This LayoutManager is not supported.");
        }
        int position = parent.getChildLayoutPosition(view);
        int column = position % spanCount;
        if (mIncludeEdge) {
            outRect.left = mSpace - column * mSpace / spanCount;
            outRect.right = (column + 1) * mSpace / spanCount;
            if (position < spanCount) {
                outRect.top = mSpace;
            }
            outRect.bottom = mSpace;
        } else {
            outRect.left = column * mSpace / spanCount;
            outRect.right = mSpace - (column + 1) * mSpace / spanCount;
            if (position >= spanCount) {
                outRect.top = mSpace;
            }
        }
    }
}
