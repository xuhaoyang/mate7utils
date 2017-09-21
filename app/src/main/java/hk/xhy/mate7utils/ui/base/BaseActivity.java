package hk.xhy.mate7utils.ui.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import hk.xhy.android.common.utils.ActivityUtils;
import hk.xhy.android.common.utils.ToastUtils;
import hk.xhy.mate7utils.R;
import hk.xhy.mate7utils.ui.interfaces.OnDialogClickListener;
import hk.xhy.mate7utils.utils.Logger;

/**
 * Created by xuhaoyang on 16/5/12.
 */
public abstract class BaseActivity extends hk.xhy.android.common.ui.BaseActivity {

    protected String TAG = getClass().getSimpleName();
    protected BaseApplication application;
    protected SharedPreferences sp;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityUtils.addActivity(this);

        /**
         * 竖屏
         */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        application = (BaseApplication) getApplication();


    }

    protected void intent2Activity(Class<? extends Activity> tarActivity) {
        ActivityUtils.startActivity(this, tarActivity);
    }

    protected void showLog(String msg) {
        Logger.show(TAG, msg);
    }

    protected void showToast(String msg) {
        ToastUtils.showShort(msg);
    }

    /**
     * 显示AlertDialog
     * @param title 标题
     * @param message 内容信息
     */
    public void showDialog(String title, String message) {
        showDialog(title, message, null);
    }

    /**
     * 显示AlertDialog
     * @param title 标题
     * @param message 内容信息
     * @param callback 对确定取消进行回调操作
     */
    public void showDialog(String title, String message, final OnDialogClickListener callback) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(message);

        if (callback != null) {
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    callback.SuccessClick();

                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }
            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    callback.CancelClick();
                }
            });
        } else {
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        final AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    /**
     * 显示AlertDialog
     * @param titleId 标题resId
     * @param messageId 内容信息resId
     * @param callback 对确定取消进行回调操作
     */
    public void showDialog(int titleId, int messageId, final OnDialogClickListener callback) {
        showDialog(getString(titleId), getString(messageId), callback);
    }

}
