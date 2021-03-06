package com.smartbracelet.sunny.ui.fragment.report;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.het.common.callback.ICallback;
import com.het.comres.view.dialog.CommonToast;
import com.smartbracelet.sunny.R;
import com.smartbracelet.sunny.base.BaseFragment;
import com.smartbracelet.sunny.biz.api.HeartRateApi;
import com.smartbracelet.sunny.manager.UserManager;
import com.smartbracelet.sunny.model.TimeBloodPressure;
import com.smartbracelet.sunny.model.UserModel;
import com.smartbracelet.sunny.model.event.HeartPressureEvent;
import com.smartbracelet.sunny.utils.DateTime;
import com.smartbracelet.sunny.utils.Json2Model;
import com.smartbracelet.sunny.utils.SunnyChartHelp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by sunny on 2015/11/12.
 * 每周报告
 */
public class ReportHeartRateFragment extends BaseFragment {

    private UserManager mUserManager;
    private UserModel mUserModel;
    private String mUserId;
    private static final String QUERY_TYPE = "day";

    private long mStartTime;
    private long mEndTime;

    @InjectView(R.id.heart_rate_chart_data)
    ColumnChartView mColumnChartView;


    //柱形图属性=================================================
    private static final int DEFAULT_DATA = 0;
    private static final int SUBCOLUMNS_DATA = 1;
    private static final int STACKED_DATA = 2;
    private static final int NEGATIVE_SUBCOLUMNS_DATA = 3;
    private static final int NEGATIVE_STACKED_DATA = 4;
    private ColumnChartData data;
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLabels = false;
    private boolean hasLabelForSelected = false;
    private int dataType = DEFAULT_DATA;

    private SunnyChartHelp mChartHelp;
    //柱形图属性结束==============================================

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_heart_rate, null);
        ButterKnife.inject(this, view);
        initParams();
        getHeartPressureByTime();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initParams() {
        mUserManager = UserManager.getInstance();
        mUserModel = mUserManager.getUserModel();
        mUserId = mUserModel == null ? "1" : mUserModel.getUserID();

        long currentDate = System.currentTimeMillis();
        mStartTime = DateTime.getFirstDayOfWeek(new Date(currentDate));
        mEndTime = DateTime.getLastDayOfWeek(new Date(currentDate));
    }

    /**
     * 获取时间段心率值
     */
    private void getHeartPressureByTime() {

        showDialog();

        new HeartRateApi().getHeartPressureByTime(new ICallback() {
            @Override
            public void onSuccess(Object o, int id) {
                hideDialog();
                parseJsonObject(o);
            }

            @Override
            public void onFailure(int code, String msg, int id) {
                handleFailure(code, msg);
            }
        }, mUserId, QUERY_TYPE, String.valueOf(mStartTime), String.valueOf(mEndTime));
    }

    private void parseJsonObject(Object o) {
        if (o == null) {
            setEmptyView();
        } else {
            List<TimeBloodPressure> timeBloodPressureList = Json2Model.parseJsonToList((String) o,
                    "heardPressure", TimeBloodPressure.class);
            generateDefaultData(timeBloodPressureList);
        }
    }

    private void setEmptyView() {
        data.setValueEmpty(mChartHelp.defNoData(true));
    }

    /**
     * 生成测试数据
     */
    private void generateDefaultData(List<TimeBloodPressure> dataes) {
        int numSubcolumns = 1;
        int numColumns = 0;
        if (dataes == null || dataes.size() == 0) {
            setEmptyView();
        } else {
            numColumns = dataes.size();
            // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
            List<Column> columns = new ArrayList<Column>();
            List<SubcolumnValue> values;
            for (int i = 0; i < numColumns; ++i) {

                values = new ArrayList<SubcolumnValue>();
                for (int j = 0; j < numSubcolumns; ++j) {
                    values.add(new SubcolumnValue((float) Math.random() * 50f + 5, ChartUtils.pickColor()));
                }

                Column column = new Column(values);
                column.setHasLabels(hasLabels);
                column.setHasLabelsOnlyForSelected(hasLabelForSelected);
                columns.add(column);
            }

            data = new ColumnChartData(columns);
            SunnyChartHelp chartHelp = new SunnyChartHelp(mContext);
            data.setValueEmpty(chartHelp.defNoData(true));

            if (hasAxes) {
                Axis axisX = new Axis();
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName("时间");
                    axisY.setName("心率");
                }
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
            } else {
                data.setAxisXBottom(null);
                data.setAxisYLeft(null);
            }

            mColumnChartView.setColumnChartData(data);
        }
    }

    private void handleFailure(int code, String msg) {
        hideDialog();
        CommonToast.showToast(mContext, msg);
    }
}
