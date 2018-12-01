package com.example.lostgoodliness.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
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

public class LostPasswordActivity extends AppCompatActivity {
    private MyCountDownTimer myCountDownTimer;
    private EditText phoneNumber;
    private EditText verifyCode;
    private Button sendcodeBt;
    private Button next;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_password);

        initView();
    }


    /**
     * 初始化界面
     */
    private void initView() {
        phoneNumber=(EditText)findViewById(R.id.phone);
        verifyCode=(EditText)findViewById(R.id.verifyCode);
        sendcodeBt=(Button)findViewById(R.id.sendcode_bt);
        next=(Button)findViewById(R.id.next);
        //只能输入数字
        phoneNumber.setInputType( InputType.TYPE_CLASS_NUMBER);
        verifyCode.setInputType( InputType.TYPE_CLASS_NUMBER);
        //new倒计时对象,总共的时间,每隔多少秒更新一次时间
        myCountDownTimer = new MyCountDownTimer(60000,
                1000,sendcodeBt);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextProgress();
            }
        });

        sendcodeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCode();
            }
        });
    }



    /**
     * 判断验证码是否正确，正确的话进行跳转
     */
    private void nextProgress() {
        final String phone=phoneNumber.getText().toString().trim();
        String code=verifyCode.getText().toString();

        if (phone.isEmpty()||code.isEmpty()){
            Toast.makeText(LostPasswordActivity.this,"手机号或验证码为空！",Toast.LENGTH_SHORT).show();
            return;
        }
        else {  //验证验证码是否正确
            BmobSMS.verifySmsCode(phone, code, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.d("hhh","验证成功："+"\n");
                        Intent intent=new Intent(LostPasswordActivity.this,SetPasswordActivity.class);
                        intent.putExtra("phone",phone);
                        intent.putExtra("userId",userId);
                        intent.putExtra("command","resetPassword");
                        startActivity(intent);

                    } else {
                        Toast.makeText(LostPasswordActivity.this,"验证码错误",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
        }
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
                    userId=list.get(0).getObjectId();
                    //发送验证码
                    BmobSMS.requestSMSCode(phone, "Lost", new QueryListener<Integer>() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if (e==null){
                                myCountDownTimer.start();
                                Toast.makeText(LostPasswordActivity.this,"验证码发送成功，" +
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
                else {
                    Toast.makeText(LostPasswordActivity.this,
                            "该手机号还未进行过注册",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }

    /**
     * 验证手机号格式正确性
     */
    public static boolean isMobileNO(String mobiles) {
        String telRegex = "[1][34578]\\d{9}";
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        }
        else {
            return mobiles.matches(telRegex);
        }
    }
}
