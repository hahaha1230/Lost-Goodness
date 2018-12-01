package com.example.lostgoodliness.utils;

import android.os.CountDownTimer;
import android.widget.Button;

/**
 * Created by 佳佳 on 2018/12/1.
 */

public class MyCountDownTimer extends CountDownTimer {
    private Button timeButton;

    public MyCountDownTimer(long millisInFuture, long countDownInterval,Button button) {
        super(millisInFuture, countDownInterval);
        this.timeButton=button;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        //防止计时过程中重复点击
        timeButton.setClickable(false);
        timeButton.setText(millisUntilFinished/1000+"秒");
    }

    @Override
    public void onFinish() {
        //重新给Button设置文字
        timeButton.setText("重新获取");
        //设置可点击
        timeButton.setClickable(true);

    }
}
