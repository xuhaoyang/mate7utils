package hk.xhy.mate7utils.ui.activity;

import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import es.dmoral.toasty.Toasty;
import hk.xhy.android.common.bind.ViewById;
import hk.xhy.android.common.utils.AppUtils;
import hk.xhy.android.common.utils.GsonUtil;
import hk.xhy.android.common.utils.ShellUtils;
import hk.xhy.mate7utils.AppConfig;
import hk.xhy.mate7utils.R;
import hk.xhy.mate7utils.event.ActivateEvent;
import hk.xhy.mate7utils.model.Item;
import hk.xhy.mate7utils.service.UtilsAccessibility;
import hk.xhy.mate7utils.service.base.BaseAccessibilityService;
import hk.xhy.mate7utils.ui.base.BaseActivity;
import hk.xhy.mate7utils.ui.interfaces.OnDialogClickListener;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @ViewById(R.id.btn_open_factory)
    AppCompatButton btn_open_factory;
    @ViewById(R.id.btn_on_liantong_4g)
    AppCompatButton btn_on_liantong_4g;
    @ViewById(R.id.btn_on_auto)
    AppCompatButton btn_on_auto;


    private boolean isAccessibilityEnabled;
    private boolean isRoot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(toolbar);

        btn_open_factory.setOnClickListener(this);
        btn_on_liantong_4g.setOnClickListener(this);
        btn_on_auto.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        isRoot = AppUtils.isAppRoot();
        Toasty.Config.getInstance().tintIcon(true).setTextSize(12).apply();
        if (AppConfig.isAppFirstRun()) {
            AppConfig.overAppFirstRunStatus();
            guideOpenAccessibility();
        }

        if (isRoot) {
            openAccessibilityByRoot();
        }
        isAccessibilityEnabled = BaseAccessibilityService.checkAccessibilityEnabled(this, UtilsAccessibility.class);

        if (AppConfig.isShowLog) {
            Toasty.info(this, "Mate7助手：" + isAccessibilityEnabled).show();
            btn_open_factory.setVisibility(View.VISIBLE);

        } else {
            btn_open_factory.setVisibility(View.GONE);
        }
    }

    private void openAccessibilityByRoot() {
        ShellUtils.execCmd("settings put secure enabled_accessibility_services hk.xhy.mate7utils/hk.xhy.mate7utils.service.UtilsAccessibility", true);
        ShellUtils.execCmd("settings put secure accessibility_enabled 1", true);
    }

    private void guideOpenAccessibility() {
        showDialog(R.string.dialog_title_accessibility_service, R.string.dialog_content_accessibility_service, new OnDialogClickListener() {
            @Override
            public void SuccessClick() {
                goAccessibility();
            }

            @Override
            public void CancelClick() {

            }
        });
    }

    private void goDevOptions() {
        startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));

    }

    private void goAccessibility() {
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }


    @Override
    public void onClick(View view) {
        Intent intent;

        if (!isAccessibilityEnabled) {
            guideOpenAccessibility();
            return;
        }
        switch (view.getId()) {
            case R.id.btn_open_factory:
                try {
//                    intent = new Intent();
//                    ComponentName cn = new ComponentName("com.android.huawei.projectmenu", "com.android.huawei.projectmenu.ProjectMenuAct");
//                    intent.setComponent(cn);
//                    startActivity(intent);
                    if (isRoot) {
                        ShellUtils.execCmd("am start -n com.android.huawei.projectmenu/.ProjectMenuAct", true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_on_liantong_4g:


                try {

                    Item result = new Item(0, "LTE", 0);
                    result.setShowname("联通4G/3G");

                    EventBus.getDefault().post(new ActivateEvent(GsonUtil.toJson(result)));
                    ShellUtils.execCmd("am start -n com.android.huawei.projectmenu/.RoamSwitchActivity", true);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.btn_on_auto:

                try {

                    Item result = new Item(1, "AUTO", 1);
                    result.setShowname("恢复电信移动网络");
                    EventBus.getDefault().post(new ActivateEvent(GsonUtil.toJson(result)));
                    ShellUtils.execCmd("am start -n com.android.huawei.projectmenu/.RoamSwitchActivity", true);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
    }
}
