package hk.xhy.mate7utils.service;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import es.dmoral.toasty.Toasty;
import hk.xhy.android.common.widget.Toaster;
import hk.xhy.mate7utils.R;
import hk.xhy.mate7utils.event.ActivateEvent;
import hk.xhy.mate7utils.model.Item;
import hk.xhy.mate7utils.service.base.BaseAccessibilityService;
import hk.xhy.mate7utils.utils.Logger;

/**
 * Created by xuhaoyang on 2017/4/20.
 */

public class UtilsAccessibility extends BaseAccessibilityService {

    private static final String TAG = UtilsAccessibility.class.getSimpleName();


    private static String developerListView = "android:id/list";
    private static String nomarlCheckbox = "android:id/checkbox";
    private static String miuiDialogTitle = "miui:id/alertTitle";
    private static String securitycenterListView = "com.miui.securitycenter:id/list_view";
    private static String securitycenterCheckbox = "com.miui.securitycenter:id/sliding_button";
    private static String securitycenterCheckbox2 = "com.miui.securitycenter:id/auto_start_sliding_button";

    private static String androidsettings = "com.android.settings";
    private static String securitycenter = "com.miui.securitycenter";
    private static String huaweiProject = "com.android.huawei.projectmenu";

    public static final String STOP_MESSAGE = "stop";
    public static final String AUTOSTART_ON = "autostart";

    private boolean checked;
    private String type;
    private boolean execute;
    private String equalPackageName;
    private String description;
    private int step;


    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void event(ActivateEvent event) {

        //是否终止执行
        if (STOP_MESSAGE.equals(event.message)) {
            execute = false;
            return;
        } else if (AUTOSTART_ON.equals(event.message)) {
            type = event.message;
            equalPackageName = securitycenter;
            step = 0;
            execute = true;
            return;
        }

        Item item = Item.parseObject(event.message);
        type = item.getRealname();
        description = item.getShowname();
        execute = true;

        //判断type
        //系统设置程序[如果是其他定制界面需改]
        equalPackageName = huaweiProject;


    }

    public AccessibilityNodeInfo recycle(AccessibilityNodeInfo node, String contains) {

        if (node.getChildCount() == 0) {
            return null;
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            String s = node.getChild(i).getClassName().toString();
            if (s.contains(contains)) {
                return node.getChild(i);
            } else {
                return recycle(node.getChild(i), contains);
            }
        }
        return null;
    }


    /**
     * 执行成功之后显示信息
     */
    public void showInformation() {
        if (checked) {
            Toaster.showShort(this, getString(R.string.closed) + description);
        } else {
            Toaster.showShort(this, getString(R.string.opened) + description);
        }
    }


    /**
     * 一次菜单点击
     *
     * @param listViewId
     * @param textId
     */
    private void executeOneMenu(String listViewId, String textId) {
        AccessibilityNodeInfo listView = findViewById(listViewId);
        AccessibilityNodeInfo temp;
        if (listView != null) {
            temp = findViewByText(textId);

            if (temp != null) {
                Logger.show(TAG, ">>>" + temp.getText());
                AccessibilityNodeInfo cb = findViewById(temp.getParent(), nomarlCheckbox);
                boolean checked = cb.isChecked();
                this.checked = checked;
                performViewClick(cb);
                showInformation();
                execute = false;
                performBackClick();
            } else {
                performScrollForward(listView);
            }
        }

    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        super.onAccessibilityEvent(event);


        if (execute) {
            if ((event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
                    event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) &&
                    event.getPackageName().equals(equalPackageName)) {

                switch (type) {
                    case "LTE":

                        AccessibilityNodeInfo lte = findViewByText("LTE", true);

                        if (lte != null) {
                            checked = performViewClick(lte);
                        }
                        execute = false;
                        showInformation();
                        performBackClick();
                        break;


                    case "AUTO":
                        AccessibilityNodeInfo auto = findViewByText("AUTO", true);
                        if (auto != null) {
                            checked = performViewClick(auto);
                        }
                        execute = false;
                        showInformation();
                        performBackClick();
                        break;

                    case AUTOSTART_ON:
//                        executeAutostartTurnOn();
                        break;

                }

            }
        }

        if (false) {
            Logger.show(TAG, "-------------------------------------------------------------");
            int eventType = event.getEventType(); //事件类型
            Logger.show(TAG, "PackageName:" + event.getPackageName() + ""); // 响应事件的包名
            Logger.show(TAG, "Source Class:" + event.getClassName() + ""); // 事件源的类名
            Logger.show(TAG, "Description:" + event.getContentDescription() + ""); // 事件源描述
            Logger.show(TAG, "Event Type(int):" + eventType + "");

            switch (eventType) {
                case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:// 通知栏事件
                    Logger.show(TAG, "event type:TYPE_NOTIFICATION_STATE_CHANGED");
                    break;
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED://窗体状态改变
                    Logger.show(TAG, "event type:TYPE_WINDOW_STATE_CHANGED");
                    break;
                case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED://View获取到焦点
                    Logger.show(TAG, "event type:TYPE_VIEW_ACCESSIBILITY_FOCUSED");
                    break;
                case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                    Logger.show(TAG, "event type:TYPE_VIEW_ACCESSIBILITY_FOCUSED");
                    break;
                case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                    Logger.show(TAG, "event type:TYPE_GESTURE_DETECTION_END");
                    break;
                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                    Logger.show(TAG, "event type:TYPE_WINDOW_CONTENT_CHANGED");
                    break;
                case AccessibilityEvent.TYPE_VIEW_CLICKED:
                    Logger.show(TAG, "event type:TYPE_VIEW_CLICKED");
                    break;
                case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                    Logger.show(TAG, "event type:TYPE_VIEW_TEXT_CHANGED");
                    break;
                case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                    Logger.show(TAG, "event type:TYPE_VIEW_SCROLLED");
                    break;
                case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                    Logger.show(TAG, "event type:TYPE_VIEW_TEXT_SELECTION_CHANGED");
                    break;
                default:
                    Logger.show(TAG, "no listen event");
            }

            for (CharSequence txt : event.getText()) {
                Logger.show(TAG, "text:" + txt);
            }

            Logger.show(TAG, "-------------------------------------------------------------");
        }
    }

    @Override
    public void onInterrupt() {

    }


}
