package com.maye.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.maye.monkeycalendarhorizontal.MonkeyCalendarHorizontal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class HomeActivity extends Activity {

    private MonkeyCalendarHorizontal mch_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initComponent();
    }

    private void initComponent() {
        mch_home = (MonkeyCalendarHorizontal) findViewById(R.id.mch_home);
        final TextView tv_home = (TextView) findViewById(R.id.tv_home);

        Calendar selectedDate = mch_home.getSelectedDate();
        tv_home.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(selectedDate.getTime()));

        Calendar instance = Calendar.getInstance();
        Calendar instance1 = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_MONTH, 1);
        List<Calendar> list = new ArrayList<>();
        list.add(instance);
        list.add(instance1);
        mch_home.setRecordList(list);

        mch_home.setOnDateSelectedListener(new MonkeyCalendarHorizontal.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Calendar date) {
                String time = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(date.getTime());
                tv_home.setText(time);
            }
        });

        mch_home.setOnMonthChangeListener(new MonkeyCalendarHorizontal.OnMonthChangeListener() {
            @Override
            public void OnMonthChange(Calendar date) {
                Toast.makeText(HomeActivity.this, new SimpleDateFormat("yyyy-MM", Locale.CHINA).format(date.getTime()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void toToday(View v){
        mch_home.backToToday();
    }

}
