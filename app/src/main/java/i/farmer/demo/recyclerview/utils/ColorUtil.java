package i.farmer.demo.recyclerview.utils;

import android.graphics.Color;

import java.util.Random;

/**
 * @author i-farmer
 * @created-time 2020/11/20 3:53 PM
 * @description 颜色工具类
 * <p>
 */
public class ColorUtil {
    /**
     * 随机颜色
     *
     * @return
     */
    public static int randomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return Color.argb(255, r, g, b);
    }

    /**
     * 取反颜色
     *
     * @param color
     * @return
     */
    public static int inverseColor(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        r = 255 - r;
        g = 255 - g;
        b = 255 - b;
        return Color.argb(255, r, g, b);
    }

    /**
     * 是否是浅色
     *
     * @param color
     * @return
     */
    public static boolean isLightColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness < 0.5) {
            return true; // It's a light color
        } else {
            return false; // It's a dark color
        }
    }
}
