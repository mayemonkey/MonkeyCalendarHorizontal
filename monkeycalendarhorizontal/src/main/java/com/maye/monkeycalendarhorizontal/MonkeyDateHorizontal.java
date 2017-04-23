package com.maye.monkeycalendarhorizontal;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MonkeyDateHorizontal extends LinearLayout {

    private LinearLayout ll_monkeydate_horizontal;
    private TextView tv_week_num;
    private TextView tv_date_num;
    private ImageView iv_date_point;

    private String[] weeks = {"Sun", "Mon", "Tue", "Wed", "Tuh", "Fri", "Sat"};
    private int year;
    private int day;
    private int month;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public MonkeyDateHorizontal(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public MonkeyDateHorizontal(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MonkeyDateHorizontal(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LinearLayout parent = new LinearLayout(context);
        parent.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ll_monkeydate_horizontal = new LinearLayout(context);
        ll_monkeydate_horizontal.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll_monkeydate_horizontal.setPadding(dp2px(context, 3), dp2px(context, 8), dp2px(context, 3), dp2px(context, 8));
        ll_monkeydate_horizontal.setBackgroundColor(Color.parseColor("#F9F9F9"));
        ll_monkeydate_horizontal.setOrientation(VERTICAL);

        LinearLayout ll = new LinearLayout(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.topMargin = dp2px(context, 5);
        ll.setLayoutParams(layoutParams);
        ll.setOrientation(VERTICAL);

        //星期
        tv_week_num = new TextView(context);
        LayoutParams params_week = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params_week.gravity = Gravity.CENTER_HORIZONTAL;
        tv_week_num.setLayoutParams(params_week);
        tv_week_num.setTextColor(Color.parseColor("#BDBDBD"));
        tv_week_num.setTypeface(Typeface.MONOSPACE);

        //日期
        tv_date_num = new TextView(context);
        LayoutParams params_date = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params_date.gravity = Gravity.CENTER_HORIZONTAL;
        params_date.topMargin = dp2px(context, 5);
        tv_date_num.setLayoutParams(params_date);
        tv_date_num.setTextColor(Color.parseColor("#111111"));
        tv_date_num.setTypeface(Typeface.MONOSPACE);

        ll.addView(tv_week_num);
        ll.addView(tv_date_num);

        ll_monkeydate_horizontal.addView(ll);

        //记录点
        iv_date_point = new ImageView(context);
        LayoutParams params_point = new LayoutParams(dp2px(context, 4), dp2px(context, 4));
        params_point.gravity = Gravity.CENTER_HORIZONTAL;
        params_point.topMargin = dp2px(context, 3);
        params_point.bottomMargin = dp2px(context, 3);
        iv_date_point.setLayoutParams(params_point);
        int roundRadius = dp2px(context, 2);
        int fillColor = Color.parseColor("#52D3C4");
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);
        iv_date_point.setBackground(gd);

        ll_monkeydate_horizontal.addView(iv_date_point);
        parent.addView(ll_monkeydate_horizontal);
        addView(parent);
    }

    /**
     * 日期文本
     *
     * @param text 文本内容
     */
    public void setDateText(CharSequence text) {
        tv_date_num.setText(text);
    }

    public void setDateTextSize(int unit, float size) {
        tv_date_num.setTextSize(unit, size);
    }

    public void setDateTextColor(int color) {
        tv_date_num.setTextColor(color);
    }

    public CharSequence getDateText() {
        return tv_date_num.getText();
    }

    /**
     * 星期文本
     *
     * @param i 星期序号
     */
    public void setWeekText(int i) {

        tv_week_num.setText(weeks[i - 1]);
    }

    public void setWeekTextSize(int unit, float size) {
        tv_week_num.setTextSize(unit, size);
    }

    public void setWeekTextColor(int color) {
        tv_week_num.setTextColor(color);
    }

    /**
     * 整体背景颜色
     */
    @Override
    public void setBackgroundColor(int color) {
        ll_monkeydate_horizontal.setBackgroundColor(color);
    }

    /**
     * 设置小点是否可见
     *
     * @param flag 判断boolean值
     */
    public void setPointVisiable(boolean flag) {
        iv_date_point.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 设置小点背景
     *
     * @param resid 资源ID
     */
    public void setPointBackground(int resid) {
        iv_date_point.setBackgroundResource(resid);
    }

    public void setPointColor(Context context, int color){
        int roundRadius = dp2px(context, 2);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(roundRadius);
        iv_date_point.setBackground(gd);
    }

    private int dp2px(Context context, int dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();

        try {
            return (int) (dp * metrics.density);
        } catch (NoSuchFieldError ignored) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
        }
    }

}
