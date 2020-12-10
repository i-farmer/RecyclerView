package i.farmer.widget.recyclerview.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author i-farmer
 * @created-time 2020/12/10 10:53 AM
 * @description 间距
 */
public class LinearSpacingDecoration extends RecyclerView.ItemDecoration {
    private int itemSpacing;            // 每个item之间的间距
    private int paddingStart;           // 整个tab的paddingStart
    private int paddingEnd;             // 整个tab的paddingEnd

    public LinearSpacingDecoration(int itemSpacing, int paddingStart, int paddingEnd) {
        this.itemSpacing = itemSpacing;
        this.paddingStart = paddingStart;
        this.paddingEnd = paddingEnd;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (!(parent.getLayoutManager() instanceof LinearLayoutManager)) {
            // 不是LinearLayoutManager
            throw new IllegalArgumentException("This LayoutManager is not supported.");
        }
        int totalCount = parent.getAdapter().getItemCount();
        int position = parent.getChildAdapterPosition(view);
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();

        if (layoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
            // 横向
            outRect.left = position == 0 ? paddingStart : 0;
            outRect.right = position == totalCount - 1 ? paddingEnd : itemSpacing;
        } else {
            // 竖向
            outRect.top = position == 0 ? paddingStart : 0;
            outRect.bottom = position == totalCount - 1 ? paddingEnd : itemSpacing;
        }
    }
}
