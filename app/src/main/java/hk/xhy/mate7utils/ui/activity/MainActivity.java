package hk.xhy.mate7utils.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import hk.xhy.android.common.bind.ViewById;
import hk.xhy.mate7utils.R;
import hk.xhy.mate7utils.ui.base.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @ViewById(R.id.btn_open_factory)
    AppCompatButton btn_open_factory;


    private boolean isAccessibilityEnabled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(toolbar);

        btn_open_factory.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_open_factory:
                intent = new Intent();
                ComponentName cn = new ComponentName("com.android.huawei.projectmenu", ".ProjectMenuAct");
                intent.setComponent(cn);
                startActivity(intent);

                break;
        }
    }
}
