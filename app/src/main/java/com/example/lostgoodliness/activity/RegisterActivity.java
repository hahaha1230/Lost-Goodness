package com.example.lostgoodliness.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.javabean.Users;
import com.example.lostgoodliness.utils.MyCountDownTimer;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class RegisterActivity extends AppCompatActivity{
    private MyCountDownTimer myCountDownTimer;
    private EditText phoneNumber;
    private EditText verifyCode;
    private Button sendCodeBt;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
    }


    /**
     * 初始化界面
     */
    private void initView() {
        phoneNumber=(EditText)findViewById(R.id.phone);
        verifyCode=(EditText)findViewById(R.id.verifyCode);
        verifyCode.setInputType( InputType.TYPE_CLASS_NUMBER);
        sendCodeBt=(Button)findViewById(R.id.sendcode_bt);
        next=(Button)findViewById(R.id.next);
        //只能输入数字
        phoneNumber.setInputType( InputType.TYPE_CLASS_NUMBER);

        //new倒计时对象,总共的时间,每隔多少秒更新一次时间
         myCountDownTimer = new MyCountDownTimer(60000,
                1000,sendCodeBt);


        sendCodeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCode();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextProgress();
            }
        });
    }



    /**
     * 判断验证码是否正确，正确的话进行跳转
     */
    private void nextProgress() {
        final String phone=phoneNumber.getText().toString().trim();
        String code=verifyCode.getText().toString();

        //验证手机号
        if (!isMobileNO(phone)){
            Toast.makeText(this,"手机号格式不正确",Toast.LENGTH_SHORT).show();
            return;
        }
        //验证验证码是否正确
        else {
            BmobSMS.verifySmsCode(phone, code, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        clearTimer();
                        Intent intent=new Intent(RegisterActivity.this,SetPasswordActivity.class);
                        intent.putExtra("phone",phone);
                        intent.putExtra("command","register");
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this,"验证码不正确",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });

        }
    }

    private void clearTimer() {
      /*  if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }*/
    }



    /**
     * 发送验证码给手机
     *
     */
    public void getCode(){
       final String phone=phoneNumber.getText().toString().trim();

       if (!isMobileNO(phone))
       {
           Toast.makeText(this,"手机号格式不正确",Toast.LENGTH_SHORT).show();
           return;
       }

        /**
         * 先进行查询，如果User里面已经存在该手机号，则已经注册，提示
         * 用密码登录，否则，发送验证码
         */
        BmobQuery<Users> query=new BmobQuery<>();
        query.addWhereEqualTo("phone",phone);
        query.findObjects(new FindListener<Users>() {
            @Override
            public void done(List<Users> list, BmobException e) {
                if (list.size()>0){
                    Toast.makeText(RegisterActivity.this,
                            "手机号已注册，请用密码登录",Toast.LENGTH_SHORT).show();
                    return;
                }
                //发送验证码
                else {
                    BmobSMS.requestSMSCode(phone, "Lost", new QueryListener<Integer>() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if (e==null){
                                myCountDownTimer.start();
                                Toast.makeText(RegisterActivity.this,"验证码发送成功，" +
                                        "请注意查收",Toast.LENGTH_SHORT).show();

                                Log.d("hhh","验证码发送成功，短信ID为："+integer+"\n");
                            }
                            else {
                                Log.d("hhh","验证码发送失败："+e.getErrorCode()+"----"
                                        +e.getMessage()+"\n");
                            }
                        }
                    });

                }

            }
        });
    }


    /**
     * 验证手机号格式正确性
     */
    public static boolean isMobileNO(String mobiles) {
	/*
	移动：134、135、136、137、138、139、150、151、147(TD)、157(TD)、158、159、178、187、188
	联通：130、131、132、152、155、156、176、185、186
	电信：133、153、177、180、189、（1349卫通）
	总结起来就是第一位必定为1，第二位必定为3、4、5、7或8，其他位置的可以为0-9
	*/
        String telRegex = "[1][34578]\\d{9}";
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        }
        else {
            return mobiles.matches(telRegex);
        }
    }




}
