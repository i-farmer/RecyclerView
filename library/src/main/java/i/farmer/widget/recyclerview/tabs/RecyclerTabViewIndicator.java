package i.farmer.widget.recyclerview.tabs;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author i-farmer
 * @created-time 2020/12/8 4:49 PM
 * @description 指示器
 */
abstract class RecyclerTabViewIndicator extends RecyclerView.ItemDecoration {
    public abstract void scrollToTabIndicator(int position, float positionOffset);

    private void checkView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("The view can not be null!");
        }
    }

    protected float getStart(View view, boolean horizontal) {
        checkView(view);
        if (horizontal) {
            return view.getLeft();
        } else {
            return view.getTop();
        }
    }

    protected float getEnd(View view, boolean horizontal) {
        checkView(view);
        if (horizontal) {
            return view.getRight();
        } else {
            return view.getBottom();
        }
    }

    protected float getSize(View view, boolean horizontal) {
        checkView(view);
        if (horizontal) {
            return view.getRight() - view.getLeft();
        } else {
            return view.getBottom() - view.getTop();
        }
    }
}
