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
    private boolean includeEdge;
    private int itemSpacing;            // 每个item之间的间距
    private int paddingLeft, paddingTop, paddingRight, paddingBottom;           // 整个tab的padding

    /**
     * @param itemSpacing item间距
     * @param padding     整个RecyclerView的padding，以下以此类推
     */
    public LinearSpacingDecoration(int itemSpacing, int padding) {
        this(false, itemSpacing, padding);
    }

    /**
     * @param includeEdge 是否包括边缘(使用padding实现)，true=边缘在视图内 false=边缘在视图外
     * @param itemSpacing
     * @param padding
     */
    public LinearSpacingDecoration(boolean includeEdge, int itemSpacing, int padding) {
        this(includeEdge, itemSpacing, padding, padding, padding, padding);
    }

    /**
     * @param itemSpacing   item间距
     * @param paddingLeft   整个RecyclerView的paddingLeft，以下以此类推
     * @param paddingTop
     * @param paddingRight
     * @param paddingBottom
     */
    public LinearSpacingDecoration(int itemSpacing, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        this(false, itemSpacing, paddingLeft, paddingTop, paddingRight, paddingBottom);
    }


    /**
     * @param includeEdge   是否包括边缘(使用padding实现)，true=边缘在视图内 false=边缘在视图外
     * @param itemSpacing   item间距
     * @param paddingLeft   整个RecyclerView的paddingLeft，以下以此类推
     * @param paddingTop
     * @param paddingRight
     * @param paddingBottom
     */
    public LinearSpacingDecoration(boolean includeEdge, int itemSpacing, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        this.includeEdge = includeEdge;
        this.itemSpacing = itemSpacing;
        this.paddingLeft = paddingLeft;
        this.paddingTop = paddingTop;
        this.paddingRight = paddingRight;
        this.paddingBottom = paddingBottom;
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

        if (includeEdge) {
            // 使用padding
            if (layoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                // 横向
                outRect.left = position == 0 ? paddingLeft / 2 : 0;
                outRect.right = position == totalCount - 1 ? paddingRight / 2 : 0;
                view.setPadding(paddingLeft / 2, paddingTop, paddingRight / 2, paddingBottom);
            } else {
                // 竖向
                outRect.top = position == 0 ? paddingTop / 2 : 0;
                outRect.bottom = position == totalCount - 1 ? paddingBottom / 2 : 0;
                view.setPadding(paddingLeft, paddingTop / 2, paddingRight, paddingBottom / 2);
            }
        } else {
            if (layoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                // 横向
                outRect.left = position == 0 ? paddingLeft : 0;
                outRect.top = paddingTop;
                outRect.right = position == totalCount - 1 ? paddingRight : itemSpacing;
                outRect.bottom = paddingBottom;
            } else {
                // 竖向
                outRect.left = paddingLeft;
                outRect.top = position == 0 ? paddingTop : 0;
                outRect.right = paddingRight;
                outRect.bottom = position == totalCount - 1 ? paddingBottom : itemSpacing;
            }
        }
    }
}
