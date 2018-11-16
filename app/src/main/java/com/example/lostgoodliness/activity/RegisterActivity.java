package com.example.lostgoodliness.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.javabean.Users;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText phoneNumber;
    private EditText verifyCode;
    private  Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        initView();
    }



    private void initView() {
        phoneNumber=(EditText)findViewById(R.id.phone);
        verifyCode=(EditText)findViewById(R.id.verifyCode);
        verifyCode.setInputType( InputType.TYPE_CLASS_NUMBER);
        //只能输入数字
        phoneNumber.setInputType( InputType.TYPE_CLASS_NUMBER);

        next=(Button)findViewById(R.id.next);
        next.setOnClickListener(this);
        verifyCode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("hhh","ontouch 事件");
                Drawable drawable=verifyCode.getCompoundDrawables()[2];
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX()>verifyCode.getWidth()-verifyCode.getPaddingRight()
                        -drawable.getIntrinsicWidth()) {
                    Log.d("hhh","ontouch 事件11");
                    getCode();
                }

                    return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next:
                nextProgress();
                break;

                default:
                    break;
        }

    }

    /**
     * 判断验证码是否正确，正确的话进行跳转
     */
    private void nextProgress() {
        final String phone=phoneNumber.getText().toString().trim();
        String code=verifyCode.getText().toString();
        if (phone.isEmpty()||code.isEmpty()){
            Toast.makeText(RegisterActivity.this,"手机号或验证码为空！",Toast.LENGTH_SHORT).show();
            return;
        }
        else {

            /**
             * 验证验证码是否正确
             */
            BmobSMS.verifySmsCode(phone, code, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.d("hhh","验证成功："+"\n");
                        Intent intent=new Intent(RegisterActivity.this,SetPasswordActivity.class);
                        intent.putExtra("phone",phone);
                        intent.putExtra("command","register");
                        startActivity(intent);

                    } else {
                        Toast.makeText(RegisterActivity.this,"验证失败",
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
        if (phone.length()!=11){
            Toast.makeText(RegisterActivity.this,"您输入的手机号有误！",Toast.LENGTH_SHORT).show();
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

}
