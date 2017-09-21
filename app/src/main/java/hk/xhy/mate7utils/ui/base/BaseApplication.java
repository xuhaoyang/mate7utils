package hk.xhy.mate7utils.ui.base;

import hk.xhy.android.common.XHYInit;
import hk.xhy.android.common.utils.CrashUtils;
import hk.xhy.android.common.utils.LogUtils;
import hk.xhy.mate7utils.BuildConfig;

/**
 * Created by xuhaoyang on 16/5/12.
 */
public class BaseApplication extends hk.xhy.android.common.Application {


    @Override
    public void onCreate() {
        super.onCreate();

        XHYInit.initialize(this);
        initLog();
        initCrash();
    }

    public static void initLog() {
        LogUtils.Builder builder = new LogUtils.Builder()
                .setLogSwitch(BuildConfig.DEBUG)// 设置log总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(BuildConfig.DEBUG)// 设置是否输出到控制台开关，默认开
                .setGlobalTag(null)// 设置log全局标签，默认为空
                // 当全局标签不为空时，我们输出的log全部为该tag，
                // 为空时，如果传入的tag为空那就显示类名，否则显示tag
                .setLogHeadSwitch(true)// 设置log头信息开关，默认为开
                .setLog2FileSwitch(false)// 打印log时是否存到文件的开关，默认关
                .setDir("")// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                .setConsoleFilter(LogUtils.V)// log的控制台过滤器，和logcat过滤器同理，默认Verbose
                .setFileFilter(LogUtils.V);// log文件过滤器，和logcat过滤器同理，默认Verbose
        LogUtils.d(builder.toString());
    }

    private void initCrash() {
        CrashUtils.init();
    }


}
