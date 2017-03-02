package com.wulee.datepicklibrary;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wulee.datepicklibrary.adapter.NumericWheelAdapter;
import com.wulee.datepicklibrary.listener.OnWheelChangedListener;
import com.wulee.datepicklibrary.utils.DateTimeUtils;

/**
 * 时间选择控件
 */
public class WheelViewDialog extends AppCompatActivity implements OnClickListener, OnWheelChangedListener{

	public static final String OLD_DATE = "old_date";
	public static final String BACK_TIME = "back_time";
	public static final String THEM_COLOR_RESOURCE = "them_color_resource";

	private WheelView yearView, // 年
			monthView, // 月
			dayView;// 日

	private Button confirm_btn, //确定
				   cancel_btn;	//取消

	private static int START_YEAR = 1900, // 起始年份
			END_YEAR = 2100; // 结束年份

	private int currentYear, // 今年
			currentMonth, // 本月
			today, // 今天
	        currHour,  //当前小时
	        currMinute;

	private String[] monthBig = { "1", "3", "5", "7", "8", "10", "12" },// 大月 31天
			monthLitle = { "4", "6", "9", "11" };// 小月 30天

	private int textSize;//滚轮文字的大小

    private int themColorRes; //主题颜色
	private TextView tvTitle,tvLine,tvYear,tvMonth,tvDay;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.com_wheel_dg);

		themColorRes = getIntent().getIntExtra(THEM_COLOR_RESOURCE,-1);

		String oldDate = null;
		try {
			oldDate = getIntent().getStringExtra(OLD_DATE);
		} catch (Exception e) {
		}
		// 初始化控件
		initView();
		// 获取现在的日期
		if (!TextUtils.isEmpty(oldDate) && DateTimeUtils.isValidDate(oldDate)) {
			parseOldDate(oldDate);
		}else{
			getCurrentDate();
		}
		//设置滚轮文字大小
		textSize=(int) (16*getResources().getDisplayMetrics().density);
		// 给控件设置初始数据
		setInitDataAddListener();

	}


	/** 初始化控件 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.wheel_title);
		tvLine = (TextView) findViewById(R.id.line);
		tvYear = (TextView) findViewById(R.id.tv_year);
		tvMonth = (TextView) findViewById(R.id.tv_month);
		tvDay = (TextView) findViewById(R.id.tv_day);

		yearView = (WheelView) findViewById(R.id.year);
		monthView = (WheelView) findViewById(R.id.month);
		dayView = (WheelView) findViewById(R.id.day);

		confirm_btn=(Button) findViewById(R.id.confirm);
		cancel_btn=(Button) findViewById(R.id.cancel);


		int themColor = ContextCompat.getColor(this,themColorRes);
		tvTitle.setTextColor(themColor);
		tvLine.setBackgroundColor(themColor);
		tvYear.setTextColor(themColor);
		tvMonth.setTextColor(themColor);
		tvDay.setTextColor(themColor);
		dayView.setSelectItemTextColor(themColor);
		ColorStateList cls = new ColorStateList(new int[][]{{android.R.attr.state_pressed},{0}}, new int[]{themColor, Color.WHITE});
		confirm_btn.setBackgroundTintList(cls);
		cancel_btn.setBackgroundTintList(cls);
	}
	
	/**解析原来设置的日期*/
	private void parseOldDate(String oldDate){
		String[] dates = oldDate.split("-");
		currentYear= Integer.parseInt(dates[0]);
		currentMonth= Integer.parseInt(dates[1]);
		today= Integer.parseInt(dates[2]);
	}

	/** 获取当前时间 */
	private void getCurrentDate() {
		String timeStr = DateTimeUtils.getStringDateTime(System.currentTimeMillis());
		String[] dates = timeStr.substring(0,10).split("-");
		currentYear= Integer.parseInt(dates[0]);
		currentMonth= Integer.parseInt(dates[1]);
		today= Integer.parseInt(dates[2]);
	}


	/** 给控件设置初始数据 */
	private void setInitDataAddListener() {
		//按钮设置监听
		confirm_btn.setOnClickListener(this);
		cancel_btn.setOnClickListener(this);
		
		// 设置年份
		yearView.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));
		yearView.setCyclic(true);
		yearView.setCurrentItem(currentYear - START_YEAR);
		yearView.addChangingListener(this);
		yearView.TEXT_SIZE = textSize;

		// 设置月份
		monthView.setAdapter(new NumericWheelAdapter(1, 12));
		monthView.setCyclic(true);
		monthView.setCurrentItem(currentMonth-1);
		monthView.addChangingListener(this);
		monthView.TEXT_SIZE = textSize;

		// 设置日期
		setDay(String.valueOf(currentMonth));
		dayView.setCyclic(true);
		dayView.setCurrentItem(today - 1);
		dayView.addChangingListener(this);
		dayView.TEXT_SIZE = textSize;
	}

	/**
	 * 判断是否包含指定的字符串
	 * 
	 * @param strs
	 *            字符串集合
	 * @param str
	 *            指定的字符
	 * @return if contain return true, or else return false.
	 * 
	 */
	private boolean isHas(String[] strs, String str) {
		if (strs != null) {
			for (String string : strs) {
				if (TextUtils.equals(str, string))
					return true;
			}
		}
		return false;
	}

	/**
	 * 需要设置的月份
	 * 
	 * @param str
	 */
	private void setDay(String str) {
		// 1,判断是大月，小月
		if (isHas(monthBig, str)) {
			dayView.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (isHas(monthLitle, str)) {
			dayView.setAdapter(new NumericWheelAdapter(1, 30));
			// 如果从大月变到小月，需要设定小月选定的日期day
			if(dayView.getCurrentItem()>=30){
				dayView.setCurrentItem(0);
		}

			// 2,都没有, 说明是2月
			// 判断是平年, 闰年	
		}else if ((currentYear % 4 == 0 && currentYear % 100 != 0)
				|| currentYear % 400 == 0) {
			dayView.setAdapter(new NumericWheelAdapter(1, 29));
			if (today>29) {
				dayView.setCurrentItem(0);
			}
		} else {
			dayView.setAdapter(new NumericWheelAdapter(1, 28));
			if (today>28) {
				dayView.setCurrentItem(0);
			}
		}
	}


	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		int wheelId = wheel.getId();
		if(wheelId == R.id.year){
			// 选中的年份
			currentYear = newValue + START_YEAR;
		}if(wheelId == R.id.month){
			// 选中的月份
			currentMonth = newValue+1;
		}if(wheelId == R.id.day){
			// 天
			today=newValue+1;
		}
		setDay(String.valueOf(currentMonth));
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.confirm){
			//返回数据
			Intent intent=new Intent();
			long unixTime = DateTimeUtils.timeToUnixDate(currentYear+"-"+currentMonth+"-"+today);
			intent.putExtra(BACK_TIME, unixTime);
			setResult(RESULT_OK, intent);
			finish();
		}if(v.getId() == R.id.cancel){
			finish();
		}
	}

}
