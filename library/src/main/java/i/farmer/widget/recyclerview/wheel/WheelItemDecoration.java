package i.farmer.widget.recyclerview.wheel;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author i-farmer
 * @created-time 2021/1/14 5:10 下午
 * @description
 */
class WheelItemDecoration extends RecyclerView.ItemDecoration {
    private Paint highlightMarkerPaint = null;          // 选中标签
    private int highlightMarkerWidth = 0;

    private Paint highlightBgPaint = null;              // 选中背景

    private Paint highlightLinePaint = null;            // 选中前后线条

    private Paint hintTextPaint = null;                 // 提示文字
    private String hintText;
    private int hintTextEndMargin;

    private WheelItemDecoration() {
    }

    /**
     * 设置选中标签
     *
     * @param width
     * @param color
     */
    private void setHighlightMarker(int width, int color) {
        if (width > 0 && color != 0) {
            this.highlightMarkerWidth = width;
            this.highlightMarkerPaint = new Paint();
            this.highlightMarkerPaint.setStyle(Paint.Style.FILL);
            this.highlightMarkerPaint.setColor(color);
        }
    }

    /**
     * 设置选中背景
     *
     * @param color
     */
    private void setHighlightBackground(int color) {
        if (color != 0) {
            this.highlightBgPaint = new Paint();
            this.highlightBgPaint.setStyle(Paint.Style.FILL);
            this.highlightBgPaint.setColor(color);
        }
    }

    /**
     * 设置选中线条
     *
     * @param width
     * @param color
     */
    private void setHighlightLine(int width, int color) {
        if (width > 0 && color != 0) {
            this.highlightLinePaint = new Paint();
            this.highlightLinePaint.setColor(color);
            this.highlightLinePaint.setStrokeWidth(width);
            this.highlightLinePaint.setAntiAlias(true);
        }
    }

    /**
     * 设置提示文字
     *
     * @param hint
     * @param size
     * @param color
     * @param marginEnd
     */
    private void setHintText(String hint, int size, int color, int marginEnd) {
        if (TextUtils.isEmpty(hint)) {
            return;
        }
        this.hintTextPaint = new Paint();

        this.hintTextPaint.setColor(color);
        this.hintTextPaint.setTextSize(size);
        this.hintTextPaint.setAntiAlias(true);
        this.hintTextEndMargin = marginEnd;
    }


    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent,
                       @NonNull RecyclerView.State state) {
        if (null == parent.getLayoutManager()
                || !(parent.getLayoutManager() instanceof WheelLayoutManager)) {
            return;
        }
        if (null != highlightBgPaint) {
            WheelLayoutManager layoutManager = (WheelLayoutManager) parent.getLayoutManager();
            float highlightLineStart = layoutManager.getOffsetSpace() + 0f;
            float highlightLineEnd = highlightLineStart + layoutManager.getItemSpace() + 0f;

            if (layoutManager.getOrientation() == WheelLayoutManager.HORIZONTAL) {
                // 横向
                float parentHeight = parent.getHeight() + 0f;
                c.drawRect(highlightLineStart, 0, highlightLineEnd, parentHeight, highlightBgPaint);
            } else {
                // 竖向
                float parentWidth = parent.getWidth() + 0f;
                c.drawRect(0f, highlightLineStart, parentWidth, highlightLineEnd, highlightBgPaint);
            }
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (null == parent.getLayoutManager()
                || !(parent.getLayoutManager() instanceof WheelLayoutManager)) {
            return;
        }
        WheelLayoutManager layoutManager = (WheelLayoutManager) parent.getLayoutManager();
        float highlightLineStart = layoutManager.getOffsetSpace() + 0f;
        float highlightLineEnd = highlightLineStart + layoutManager.getItemSpace() + 0f;

        if (null != highlightLinePaint) {
            if (layoutManager.getOrientation() == WheelLayoutManager.HORIZONTAL) {
                // 横向
                float parentHeight = parent.getHeight() + 0f;
                c.drawLine(highlightLineStart, 0, highlightLineStart, parentHeight, highlightLinePaint);
                c.drawLine(highlightLineEnd, 0, highlightLineEnd, parentHeight, highlightLinePaint);
            } else {
                // 竖向
                float parentWidth = parent.getWidth() + 0f;
                c.drawLine(0f, highlightLineStart, parentWidth, highlightLineStart, highlightLinePaint);
                c.drawLine(0f, highlightLineEnd, parentWidth, highlightLineEnd, highlightLinePaint);
            }
        }

        if (null != highlightMarkerPaint) {
            if (layoutManager.getOrientation() == WheelLayoutManager.HORIZONTAL) {
                // 横向
                c.drawRect(
                        highlightLineStart, 0f, highlightLineEnd,
                        highlightMarkerWidth, highlightMarkerPaint
                );
            } else {
                // 竖向
                c.drawRect(
                        0f, highlightLineStart, highlightMarkerWidth,
                        highlightLineEnd, highlightMarkerPaint
                );
            }
        }
        if (!TextUtils.isEmpty(hintText) && null != hintTextPaint) {
            float textX;
            float textY;

            Paint.FontMetricsInt fontMetricsInt = this.hintTextPaint.getFontMetricsInt();
            if (layoutManager.getOrientation() == WheelLayoutManager.HORIZONTAL) {
                // 横向
                textX = highlightLineStart + (layoutManager.getItemSpace() - hintTextPaint.measureText(hintText)) / 2;
                textY = parent.getHeight() - hintTextEndMargin - fontMetricsInt.bottom;
            } else {
                // 竖向
                textX = parent.getWidth() - hintTextPaint.measureText(hintText) - hintTextEndMargin;

                int hintTextDrawingOffsetY = -(fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.top;
                textY = highlightLineStart + layoutManager.getItemSpace() / 2 + hintTextDrawingOffsetY;
            }
            c.drawText(hintText, textX, textY, hintTextPaint);
        }
    }

    /**
     * Builder模式
     */
    public static class Builder {
        private int markerWidth = 0;
        private int markerColor = 0;
        private int highlightBackgroundColor = 0;
        private int highlightLineWidth = 0;
        private int highlightLineColor = 0;

        private String hint;
        private int hintSize = 16;
        private int hintColor = 0XFF1A1A1A;
        private int hintMarginEnd = 0;

        public Builder setHighlightMarker(int width, int color) {
            this.markerWidth = width;
            this.markerColor = color;
            return this;
        }

        public Builder setHighlightBackground(int color) {
            this.highlightBackgroundColor = color;
            return this;
        }

        public Builder setHighlightLine(int width, int color) {
            this.highlightLineWidth = width;
            this.highlightLineColor = color;
            return this;
        }

        public Builder setHintText(String hint, int size, int color, int marginEnd) {
            this.hint = hint;
            this.hintSize = size;
            this.hintColor = color;
            this.hintMarginEnd = marginEnd;
            return this;
        }

        public WheelItemDecoration build() {
            WheelItemDecoration decoration = new WheelItemDecoration();
            decoration.setHighlightMarker(markerWidth, markerColor);
            decoration.setHighlightBackground(highlightBackgroundColor);
            decoration.setHighlightLine(highlightLineWidth, highlightLineColor);
            decoration.setHintText(hint, hintSize, hintColor, hintMarginEnd);
            return decoration;
        }
    }
}
