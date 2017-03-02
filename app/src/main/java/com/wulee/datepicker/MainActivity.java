package com.wulee.datepicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wulee.datepicklibrary.WheelViewDialog;
import com.wulee.datepicklibrary.utils.DateTimeUtils;

public class MainActivity extends AppCompatActivity {

    private static final int TIME_REQUEST_CODE = 100; // 日期的请求码

    private TextView tvReturnDate;
    private Button btnDatePick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvReturnDate = (TextView) findViewById(R.id.tv_return_date);
        btnDatePick = (Button) findViewById(R.id.btn_pick_date);
        btnDatePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, WheelViewDialog.class).putExtra(WheelViewDialog.THEM_COLOR_RESOURCE,R.color.colorAccent), TIME_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TIME_REQUEST_CODE:// 日期的返回
                if (resultCode == RESULT_OK && data != null) {
                    long unixTime = data.getLongExtra(WheelViewDialog.BACK_TIME, 0L);
                    if(unixTime > 0 ){
                        String sBirthday = DateTimeUtils.getStringDate(unixTime);
                        tvReturnDate.setText(sBirthday);
                    }
                }
                break;
        }
    }
}
