package hk.xhy.mate7utils;

import hk.xhy.android.common.utils.PreferenceUtils;

/**
 * Created by xuhaoyang on 2017/9/21.
 */

public class AppConfig extends PreferenceUtils {
    private static final String TAG = AppConfig.class.getSimpleName();

    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final boolean isShowLog = DEBUG;

    public static boolean isAppFirstRun() {
        return getBoolean("AppFirstRun", true);
    }

    public static boolean getFirstGuide() {
        return getBoolean("FirstGuide", false);
    }

    public static void overFirstGuide() {
        putBoolean("FirstGuide", true);
    }

    public static void overAppFirstRunStatus() {
        overAppFirstRunStatus(false);
    }

    public static void overAppFirstRunStatus(boolean flags) {
        putBoolean("AppFirstRun", flags);
    }

}
