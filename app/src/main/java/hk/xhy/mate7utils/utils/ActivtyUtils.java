package hk.xhy.mate7utils.utils;

import android.content.Intent;

import hk.xhy.android.common.utils.ActivityUtils;
import hk.xhy.mate7utils.ui.activity.MainActivity;

/**
 * Created by xuhaoyang on 16/8/24.
 */
public class ActivtyUtils extends ActivityUtils {

    public static void backHomeActivity() {
        backHomeActivity(-1);
    }

    public static void backHomeActivity(int tabId) {
        try {
            for (int i = activityStack.size() - 1; i >= 0; i--) {
                if (null != activityStack.get(i)) {
                    if (activityStack.get(i).getClass() == MainActivity.class) {
                        if (tabId != -1) {
                            Intent intent = new Intent();
                            intent.putExtra("tab_id", tabId);
                            activityStack.get(i).setIntent(intent);
                        }
                        continue;
                    }
                    finishActivity(activityStack.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
