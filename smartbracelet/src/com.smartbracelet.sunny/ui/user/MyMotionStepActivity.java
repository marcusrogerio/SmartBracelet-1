package com.smartbracelet.sunny.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.het.common.callback.ICallback;
import com.smartbracelet.sunny.AppConstant;
import com.smartbracelet.sunny.R;
import com.smartbracelet.sunny.base.BaseActivity;
import com.smartbracelet.sunny.biz.api.HeartRateApi;
import com.smartbracelet.sunny.biz.api.StepApi;
import com.smartbracelet.sunny.manager.UserManager;
import com.smartbracelet.sunny.model.UserModel;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by sunny on 2015/11/17.
 * 我的运动计步
 */
public class MyMotionStepActivity extends BaseActivity {
    private static final String TAG = MyMotionStepActivity.class.getSimpleName();

    @InjectView(R.id.smart_bracelet_topbar)
    RelativeLayout commonTopBar;
    @InjectView(R.id.smart_bracelet_title)
    TextView mTitle;
    @InjectView(R.id.left_image)
    ImageView mBack;

    private UserManager mUserManager;
    private UserModel mUserModel;

    public static void startMyMotionStepActivity(Context context) {
        Intent targetIntent = new Intent(context, MyMotionStepActivity.class);
        context.startActivity(targetIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_motion_step);
    }

    @Override
    public void initParams() {
        super.initParams();
        mUserManager = UserManager.getInstance();
        mUserModel = mUserManager.getUserModel();
    }

    @Override
    public void initTitleBar() {
        commonTopBar.setBackgroundColor(0xFF22AB63);
        mTitle.setText(getResources().getString(R.string.user_my_step));
    }


    @Override
    protected void onResume() {
        super.onResume();
        getTodayHeartRateData();
    }

    /**
     * 获取当天的心率信息
     */
    private void getTodayHeartRateData() {

        String userId = mUserModel == null ? AppConstant.User.USE_ID_DEF : mUserModel.getUserID();
        new StepApi().getStep(new ICallback<String>() {
            @Override
            public void onSuccess(String o, int id) {

            }

            @Override
            public void onFailure(int code, String msg, int id) {

            }
        }, userId);
    }


    @OnClick(R.id.left_image)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
        }
    }
}
