package i.farmer.demo.recyclerview.utils;

import android.view.MotionEvent;
import android.view.View;

public class ButtonClickUtil {
    /**
     * 按钮点击效果
     *
     * @param views
     */
    public static void setAlphaChange(final View... views) {
        for (View view : views) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!v.isEnabled()) {
                        return false;
                    }
                    if (!v.isClickable()) {
                        return false;
                    }
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.setAlpha(0.6f);
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            v.setAlpha(1.0f);
                            break;
                    }
                    return false;
                }
            });
        }
    }
}
