package i.farmer.demo.recyclerview.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author i-farmer
 * @created-time 2020/11/20 4:48 PM
 * @description 页面跳转
 * <p>
 */
public class JumpUtil {

    public static void enter(Context context, Class<? extends Activity> clz) {
        // 跳转
        enter(context, clz, null);
    }

    public static void enter(Context context, Class<? extends Activity> clz, Bundle bundle) {
        // 跳转
        Intent intent = new Intent(context, clz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    public static void enter(Activity activity, Class<? extends Activity> clz, int requestCode) {
        // 跳转 ForResult
        enter(activity, clz, requestCode, null);
    }

    public static void enter(Activity activity, Class<? extends Activity> clz, int requestCode, Bundle bundle) {
        // 跳转 ForResult
        Intent intent = new Intent(activity, clz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        activity.startActivityForResult(intent, requestCode);
    }
}
