package com.maye.monkeycalendarhorizontal;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.view.Gravity.CENTER;

public class MonkeyCalendarHorizontal extends FrameLayout {
    private HorizontalScrollView sv_monkeycalendar;
    private LinearLayout ll_monkeycalendar_horizontal;
    private MonkeyDateHorizontal mPreviousSelectedDate;

    private Calendar date_now = Calendar.getInstance();
    private Calendar date_selected = Calendar.getInstance();
    private Context context;

    private ArrayList<Calendar> list_record;
    private List<MonkeyDateHorizontal> list_date = new ArrayList<>();
    private int windowWidth;
    private int startX;
    private int offsetLeft = 0;
    private int dateWidth;
    private int width;

    //颜色
    private int todayColor;
    private int tipColor;
    private int dateColor;
    private int selectTipColor;
    private int selectDateColor;
    private int backgroundColor;
    private int selectBackgroundColor;
    private int pointColor;
    private int selectPointColor;

    public interface OnMonthChangeListener {
        void OnMonthChange(Calendar date);
    }

    private OnMonthChangeListener mMonthChangeListener;

    public void setOnMonthChangeListener(OnMonthChangeListener mMonthChangeListener) {
        this.mMonthChangeListener = mMonthChangeListener;
    }

    public interface OnDateSelectedListener {
        void onDateSelected(Calendar date);
    }

    private OnDateSelectedListener mDateSelectedListener;

    public void setOnDateSelectedListener(OnDateSelectedListener mDateSelectedListener) {
        this.mDateSelectedListener = mDateSelectedListener;
    }

    public MonkeyCalendarHorizontal(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    public MonkeyCalendarHorizontal(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        setWindowWidth();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MonkeyCalendarHorizontal);
        if (typedArray != null) {
            todayColor = typedArray.getColor(R.styleable.MonkeyCalendarHorizontal_todayColor, Color.parseColor("#D73C10"));
            tipColor = typedArray.getColor(R.styleable.MonkeyCalendarHorizontal_tipColor, Color.parseColor("#BDBDBD"));
            dateColor = typedArray.getColor(R.styleable.MonkeyCalendarHorizontal_dateColor, Color.parseColor("#111111"));
            pointColor = typedArray.getColor(R.styleable.MonkeyCalendarHorizontal_pointColor, Color.parseColor("#52D3C4"));
            backgroundColor = typedArray.getColor(R.styleable.MonkeyCalendarHorizontal_backgroundColor, Color.parseColor("#F9F9F9"));
            selectTipColor = typedArray.getColor(R.styleable.MonkeyCalendarHorizontal_selectTipColor, Color.parseColor("#FFFFFF"));
            selectDateColor = typedArray.getColor(R.styleable.MonkeyCalendarHorizontal_selectDateColor, Color.parseColor("#FFFFFF"));
            selectBackgroundColor = typedArray.getColor(R.styleable.MonkeyCalendarHorizontal_selectBackgroundColor, Color.parseColor("#52D3C4"));
            selectPointColor = typedArray.getColor(R.styleable.MonkeyCalendarHorizontal_selectPointColor, Color.parseColor("#FFFFFF"));

            typedArray.recycle();
        }

        init(context);
    }

    public MonkeyCalendarHorizontal(Context context) {
        super(context);
        this.context = context;
        setWindowWidth();
        init(context);
    }

    private void init(Context context) {
        sv_monkeycalendar = new HorizontalScrollView(context);
        sv_monkeycalendar.requestDisallowInterceptTouchEvent(true);
        sv_monkeycalendar.setOnTouchListener(new ScrollListener());
        sv_monkeycalendar.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        sv_monkeycalendar.setOverScrollMode(OVER_SCROLL_NEVER);
        sv_monkeycalendar.setHorizontalScrollBarEnabled(false);

        ll_monkeycalendar_horizontal = new LinearLayout(context);
        ll_monkeycalendar_horizontal.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        ll_monkeycalendar_horizontal.setOrientation(LinearLayout.HORIZONTAL);

        sv_monkeycalendar.addView(ll_monkeycalendar_horizontal);

        addView(sv_monkeycalendar);

        fillDateView(true);
    }

    /**
     * 添加
     */
    @SuppressWarnings("WrongConstant")
    private void fillDateView(boolean move) {
        // 清除所有子控件
        ll_monkeycalendar_horizontal.removeAllViews();

        // 填充日历内容
        dateWidth = windowWidth / 7;

        MonkeyDateHorizontal date;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        lp.width = dateWidth;

        int mostdate = date_now.getActualMaximum(Calendar.DAY_OF_MONTH);
        width = dateWidth * mostdate;
        Log.e("Calendar", "Total：" + width);
        int day = 1;
        list_date.clear();
        for (int i = 0; i < mostdate; i++) {
            date = new MonkeyDateHorizontal(context);
            date.setLayoutParams(lp);
            date.setBackgroundColor(backgroundColor);
            date.setGravity(CENTER);
            // 星期字体
            date.setWeekTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            date.setWeekTextColor(tipColor);
            // 日期字体
            date.setDateTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            date.setDateTextColor(dateColor);
            date.setPointVisiable(true);

            // 设置年月日
            date.setYear(date_now.get(Calendar.YEAR));
            date.setMonth(date_now.get(Calendar.MONTH));
            date.setDay(day);

            date_now.set(Calendar.DAY_OF_MONTH, day);

            // 当前日期是否存在记录
            if (isRecord(date_now)) {
                date.setPointVisiable(true);
            } else {
                date.setPointVisiable(false);
            }

            // 今天
            if (isToday(date_now)) {
                date.setWeekTextColor(tipColor);
                date.setDateTextColor(todayColor);
                if (date_selected.get(Calendar.MONTH) == date_now.get(Calendar.MONTH)
                        && date_selected.get(Calendar.DAY_OF_MONTH) == day) { // 本日且被选中
                    mPreviousSelectedDate = date;
                    date.setWeekTextColor(selectTipColor);
                    date.setBackgroundColor(selectBackgroundColor);
                    date.setPointColor(context, selectPointColor);
                }
                if (move) {
                    // 在中间移动范围内
                    if (date_now.get(Calendar.DAY_OF_MONTH) > 4 && date_now.get(Calendar.DAY_OF_MONTH) <= mostdate - 3) {
                        final int day_of_month = date_now.get(Calendar.DAY_OF_MONTH) - 4;
                        sv_monkeycalendar.post(new Runnable() {
                            @Override
                            public void run() {
                                sv_monkeycalendar.scrollTo(dateWidth * day_of_month, 0);
                            }
                        });
                    }

                    // 在后段范围内
                    if (date_now.get(Calendar.DAY_OF_MONTH) > (mostdate - 3) && date_now.get(Calendar.DAY_OF_MONTH) <= mostdate) {
                        final int day_of_month = mostdate - 7;
                        sv_monkeycalendar.post(new Runnable() {
                            @Override
                            public void run() {
                                sv_monkeycalendar.scrollTo(dateWidth * day_of_month, 0);
                            }
                        });
                    }
                }
            }
            // 选中
            else if (date_selected.get(Calendar.MONTH) == date_now.get(Calendar.MONTH)
                    && date_selected.get(Calendar.DAY_OF_MONTH) == day) {
                mPreviousSelectedDate = date;
                date.setWeekTextColor(selectTipColor);
                date.setDateTextColor(selectDateColor);
//                date.setPointBackground(R.drawable.circle_blue);
                mPreviousSelectedDate.setPointColor(context, selectPointColor);
                date.setBackgroundColor(selectBackgroundColor);
            }
            date.setDateText(String.valueOf(day++));
            date.setWeekText(date_now.get(Calendar.DAY_OF_WEEK));
            ll_monkeycalendar_horizontal.addView(date);
            list_date.add(date);
        }
        requestLayout();
    }

    // 获取选中的日期
    public Calendar getSelectedDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, date_selected.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, date_selected.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, date_selected.get(Calendar.DAY_OF_MONTH));
        return calendar;
    }

    /**
     * 是否存在记录
     */
    private boolean isRecord(Calendar date) {
        if (list_record != null) {
            for (Calendar record_date : list_record) {
                // 当前日期存在记录
                if (date.get(Calendar.YEAR) == record_date.get(Calendar.YEAR)
                        && date.get(Calendar.MONTH) == record_date.get(Calendar.MONTH)
                        && date.get(Calendar.DAY_OF_MONTH) == record_date.get(Calendar.DAY_OF_MONTH)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是今天
     */
    private boolean isToday(Calendar date) {
        Calendar today = Calendar.getInstance();

        return date.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && date.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && date.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 方法重载
     */
    private boolean isToday(int yaer, int month, int day) {
        Calendar today = Calendar.getInstance();

        return yaer == today.get(Calendar.YEAR) && month == today.get(Calendar.MONTH)
                && day == today.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 设置设备的宽度
     */
    public void setWindowWidth() {
        Activity activity = (Activity) context;
        Display d = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        d.getMetrics(dm);
        windowWidth = dm.widthPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取日期控件显示宽度
        int width = MeasureSpec.getSize(widthMeasureSpec);
        dateWidth = width / 7;
        Log.e("Calendar", "Date：" + dateWidth);
    }

    private void clickDate(MonkeyDateHorizontal tv) {
        getParent().requestDisallowInterceptTouchEvent(false);
        Calendar selected = Calendar.getInstance();

        // 日期
        selected.set(Calendar.YEAR, tv.getYear());
        selected.set(Calendar.MONTH, tv.getMonth());
        selected.set(Calendar.DAY_OF_MONTH, tv.getDay());
        if (!isToday(selected)) { // 选中的不是当天
            tv.setDateTextColor(selectDateColor);
        } else {
            tv.setDateTextColor(todayColor);
        }
        tv.setWeekTextColor(selectTipColor);
        tv.setPointColor(context, selectPointColor);

        tv.setBackgroundColor(selectBackgroundColor);
        if (mPreviousSelectedDate != null) {
            if (mPreviousSelectedDate != tv) {
                try {
                    if (isToday(mPreviousSelectedDate.getYear(), mPreviousSelectedDate.getMonth(),
                            mPreviousSelectedDate.getDay())) {
                        mPreviousSelectedDate.setWeekTextColor(tipColor);
                        mPreviousSelectedDate.setDateTextColor(todayColor);
                        mPreviousSelectedDate.setPointColor(context, pointColor);
                        mPreviousSelectedDate.setBackgroundColor(backgroundColor);
                    } else {
                        mPreviousSelectedDate.setWeekTextColor(tipColor);
                        mPreviousSelectedDate.setDateTextColor(dateColor);
                        mPreviousSelectedDate.setPointColor(context, pointColor);
                        mPreviousSelectedDate.setBackgroundColor(backgroundColor);
                    }
                } catch (Exception ex) {
                    mPreviousSelectedDate.setWeekTextColor(tipColor);
                    mPreviousSelectedDate.setDateTextColor(dateColor);
                    mPreviousSelectedDate.setPointColor(context, pointColor);
                    mPreviousSelectedDate.setBackgroundColor(backgroundColor);
                }
            }
        }
        // 设置选中的日期
        int selectedDay = Integer.parseInt((tv.getDateText().toString()));
        date_selected.set(Calendar.YEAR, date_now.get(Calendar.YEAR));
        date_selected.set(Calendar.MONTH, date_now.get(Calendar.MONTH));
        date_selected.set(Calendar.DAY_OF_MONTH, selectedDay);
        mPreviousSelectedDate = tv;
        if (mDateSelectedListener != null)
            mDateSelectedListener.onDateSelected(date_selected);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 设置记录集合
     *
     * @param list
     */
    public void setRecordList(List<Calendar> list) {
        list_record = (ArrayList<Calendar>) list;
        fillDateView(false);
    }

    /**
     * 返回当天
     */
    public void backToToday() {
        Calendar calendar = Calendar.getInstance();
        offsetLeft = 0;
        date_now = calendar;
        fillDateView(true);
        if (mMonthChangeListener != null) {
            mMonthChangeListener.OnMonthChange(date_now);
        }
    }

    /**
     * 改变日期为
     */
    public void changeDateTo(Calendar calendar) {
        // 同年变化
        if (calendar.get(Calendar.YEAR) == date_now.get(Calendar.YEAR)) {
            if (calendar.get(Calendar.MONTH) != date_now.get(Calendar.MONTH)) { // 不同月变化
                date_now = calendar;
                fillDateView(true);
                ll_monkeycalendar_horizontal.scrollTo(0, 0);
                if (calendar.get(Calendar.MONTH) > date_now.get(Calendar.MONTH)) { // 下月
                    TranslateAnimation ta = new TranslateAnimation(0.0F, -windowWidth, 0.0F, 0.0F);
                    ta.setDuration(500);
                    ta.setRepeatMode(TranslateAnimation.RESTART);
                    ll_monkeycalendar_horizontal.startAnimation(ta);
                }
                if (calendar.get(Calendar.MONTH) < date_now.get(Calendar.MONTH)) { // 上月
                    TranslateAnimation ta = new TranslateAnimation(0.0F, -windowWidth, 0.0F, 0.0F);
                    ta.setDuration(500);
                    ta.setRepeatMode(TranslateAnimation.RESTART);
                    ll_monkeycalendar_horizontal.startAnimation(ta);
                }
                if (this.mMonthChangeListener != null) {
                    this.mMonthChangeListener.OnMonthChange(date_now);
                }
            }
        } else {    //不同年
            date_now = calendar;
            fillDateView(true);
            ll_monkeycalendar_horizontal.scrollTo(0, 0);
            if (calendar.get(Calendar.YEAR) > date_now.get(Calendar.YEAR)) {
                TranslateAnimation ta = new TranslateAnimation(0.0F, -windowWidth, 0.0F, 0.0F);
                ta.setDuration(500);
                ta.setRepeatMode(TranslateAnimation.RESTART);
                ll_monkeycalendar_horizontal.startAnimation(ta);
            }
            if (calendar.get(Calendar.YEAR) < date_now.get(Calendar.YEAR)) {
                TranslateAnimation ta = new TranslateAnimation(0.0F, -windowWidth, 0.0F, 0.0F);
                ta.setDuration(500);
                ta.setRepeatMode(TranslateAnimation.RESTART);
                ll_monkeycalendar_horizontal.startAnimation(ta);
            }
            if (this.mMonthChangeListener != null) {
                this.mMonthChangeListener.OnMonthChange(date_now);
            }
        }
    }

    class ScrollListener implements OnTouchListener {

        private int start;
        private boolean isOverScroll;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    start = (int) event.getX();
                    startX = (int) event.getX();
                    if (sv_monkeycalendar.getScrollX() == 0 || sv_monkeycalendar.getScrollX() == width - windowWidth) {
                        isOverScroll = true;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    int moveX = (int) event.getX();

                    if ((sv_monkeycalendar.getScrollX() == 0 || sv_monkeycalendar.getScrollX() == width - windowWidth) && isOverScroll) {
                        int offset = moveX - startX;
                        ll_monkeycalendar_horizontal.scrollBy(-offset, 0);
                        offsetLeft += -offset;
                        Log.e("offsetLeft", offsetLeft + "");
                        startX = (int) event.getX();
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    isOverScroll = false;
                    ll_monkeycalendar_horizontal.scrollTo(0, 0);

                    if ((int) event.getX() == start) {                      //点击日期
                        int position = sv_monkeycalendar.getScrollX() + start;
                        int index = (position / dateWidth);
                        clickDate(list_date.get(index));

                    } else if (offsetLeft < -windowWidth / 2) {             //切换至上月
                        date_now.add(Calendar.MONTH, -1);
                        date_now.set(Calendar.DAY_OF_MONTH, 1);
                        fillDateView(false);
                        int mostday = date_now.getActualMaximum(Calendar.DAY_OF_MONTH);

                        // 动画效果
                        TranslateAnimation ta = new TranslateAnimation(-windowWidth, 0, 0, 0);
                        ta.setDuration(500);
                        ta.setRepeatMode(Animation.RESTART);
                        sv_monkeycalendar.startAnimation(ta);
                        // 归零数据
                        offsetLeft = 0;
                        // 移动位置

                        final int scroll = dateWidth * (mostday - 7);
                        sv_monkeycalendar.post(new Runnable() {
                            @Override
                            public void run() {
                                sv_monkeycalendar.scrollTo(scroll, 0);
                            }
                        });
                        if (mMonthChangeListener != null) {
                            mMonthChangeListener.OnMonthChange(date_now);
                        }
                        Log.e("scroll", "scrollTo=" + dateWidth * (mostday - 7) + "      right=" + sv_monkeycalendar.getScrollX());

                    } else if (offsetLeft > windowWidth / 2) {
                        date_now.add(Calendar.MONTH, 1);
                        date_now.set(Calendar.DAY_OF_MONTH, 1);
                        // 动画效果
                        ValueAnimator valueAnimator = ValueAnimator.ofInt(offsetLeft, width).setDuration(150);
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int value = (int) animation.getAnimatedValue();
                                ll_monkeycalendar_horizontal.scrollTo(value, 0);
                            }
                        });
                        valueAnimator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                fillDateView(false);
                                sv_monkeycalendar.scrollTo(0, 0);
                                ValueAnimator valueAnimator = ValueAnimator.ofInt(-windowWidth, 0).setDuration(350);
                                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        int value = (int) animation.getAnimatedValue();
                                        ll_monkeycalendar_horizontal.scrollTo(value, 0);
                                    }
                                });
                                valueAnimator.start();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                        valueAnimator.start();

                        // 归零数据
                        offsetLeft = 0;
                        if (mMonthChangeListener != null) {
                            mMonthChangeListener.OnMonthChange(date_now);
                        }

                    } else {                                                //恢复本月
                        offsetLeft = 0;
                    }
                    break;
            }

            return false;
        }
    }

}
