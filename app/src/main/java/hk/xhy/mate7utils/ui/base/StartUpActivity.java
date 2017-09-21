package hk.xhy.mate7utils.ui.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.Animation;

import hk.xhy.mate7utils.utils.ActivtyUtils;


/**
 * Created by xuhaoyang on 16/9/8.
 */
public class StartUpActivity extends hk.xhy.android.common.ui.StartUpActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivtyUtils.addActivity(this);
        //禁止横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }
}
