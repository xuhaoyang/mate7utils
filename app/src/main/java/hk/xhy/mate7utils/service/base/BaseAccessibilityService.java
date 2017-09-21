package hk.xhy.mate7utils.service.base;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import hk.xhy.mate7utils.utils.Logger;


public class BaseAccessibilityService extends AccessibilityService {

    private static final String TAG = BaseAccessibilityService.class.getSimpleName();

    private AccessibilityManager mAccessibilityManager;
    private Context mContext;
    private static BaseAccessibilityService mInstance;

    public static boolean checkAccessibilityEnabled(Context mContext, Class clazz) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + clazz.getCanonicalName();
        Logger.show(TAG, "service:" + service);
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
            Logger.show(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Logger.show(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            // com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Logger.show(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Logger.show(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
        }
        return false;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mAccessibilityManager = (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    public static BaseAccessibilityService getInstance() {
        if (mInstance == null) {
            mInstance = new BaseAccessibilityService();
        }
        return mInstance;
    }

    /**
     * Check当前辅助服务是否启用
     *
     * @param serviceName serviceName
     * @return 是否启用
     */
    public boolean checkAccessibilityEnabled(String serviceName) {
        List<AccessibilityServiceInfo> accessibilityServices =
                mAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 前往开启辅助服务界面
     */
    public void goAccess() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }


    /**
     * 阻塞线程1秒
     */
    private void pause() {
        pause(1000);
    }

    /**
     * 阻塞线程
     *
     * @param time
     */
    private void pause(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟点击事件
     *
     * @param nodeInfo nodeInfo
     */
    public boolean performViewClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return false;
        }
        while (nodeInfo != null) {
            if (nodeInfo.isClickable()) {
                return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            nodeInfo = nodeInfo.getParent();
        }
        return false;
    }

    /**
     * 模拟返回操作
     */
    public void performBackClick() {
        performBackClick(100);
    }

    /**
     * 模拟返回操作
     *
     * @param time
     */
    public void performBackClick(long time) {
        pause(time);
        performGlobalAction(GLOBAL_ACTION_BACK);
    }


    /**
     * 模拟下滑操作
     */
    public void performScrollBackward() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
    }

    /**
     * 模拟下滑操作
     */
    public void performScrollBackward(AccessibilityNodeInfo info) {
        if (info == null) {
            return;
        }
        info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
    }

    /**
     * 模拟上滑操作
     */
    public void performScrollForward() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
    }

    /**
     * 模拟上滑操作
     */
    public void performScrollForward(AccessibilityNodeInfo info) {
        if (info == null) {
            return;
        }
        info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
    }

    /**
     * 查找对应文本的View(默认不可点击)
     *
     * @param text text
     * @return View
     */
    public AccessibilityNodeInfo findViewByText(String text) {
        return findViewByText(text, false);
    }

    /**
     * 查找对应文本的View
     *
     * @param text      text
     * @param clickable 该View是否可以点击
     * @return View
     */
    public AccessibilityNodeInfo findViewByText(String text, boolean clickable) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && (nodeInfo.isClickable() == clickable)) {
                    if (text.equals(nodeInfo.getText())){
                        return nodeInfo;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 查找对应ID的View
     *
     * @param id id
     * @return View
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityNodeInfo findViewById(String id) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        return findViewById(accessibilityNodeInfo, id);
    }

    /**
     * 查找对应ID的View
     *
     * @param accessibilityNodeInfo 目标节点中
     * @param id
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityNodeInfo findViewById(AccessibilityNodeInfo accessibilityNodeInfo, String id) {
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    public void clickTextViewByText(String text) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performViewClick(nodeInfo);
                    break;
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void clickTextViewByID(String id) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performViewClick(nodeInfo);
                    break;
                }
            }
        }
    }

    /**
     * 模拟输入
     *
     * @param nodeInfo nodeInfo
     * @param text     text
     */
    public void inputText(AccessibilityNodeInfo nodeInfo, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", text);
            clipboard.setPrimaryClip(clip);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    public Context getmContext() {
        return mContext;
    }
}
