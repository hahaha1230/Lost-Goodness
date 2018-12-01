package com.example.lostgoodliness.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.javabean.Users;
import com.example.lostgoodliness.manage.UserManage;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class LoginActivity extends AppCompatActivity {
    private EditText phoneNumber;
    private EditText passwordText;
    private TextView noAccount;
    private TextView lostPassword;
    private Checkable passwordRemember;
    private Checkable autoLogin;
    private Button login;
    //保存账号和密码的文件
    private File file = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        initData();
    }


    /**
     * 初始化界面
     */
    private void initView() {
        phoneNumber = (EditText) findViewById(R.id.phone);
        passwordText = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        noAccount = (TextView) findViewById(R.id.noAccount);
        lostPassword = (TextView) findViewById(R.id.lostPassword);
        passwordRemember = (Checkable) findViewById(R.id.remember_password);
        autoLogin = (Checkable) findViewById(R.id.auto_login);
        //设置隐藏密码
        passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        //手机号里面只能输入数字
        phoneNumber.setInputType(InputType.TYPE_CLASS_NUMBER);

        //点击忘记密码跳转到找回密码界面
        lostPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, LostPasswordActivity.class);
                startActivity(intent);
            }
        });


        //点击登录进行验证登录
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        //点击注册跳转到注册界面
        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        //处理密码框右边图片的点击事件，判断是否进行显示密码
        passwordText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable = passwordText.getCompoundDrawables()[2];
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > passwordText.getWidth() - passwordText.getPaddingRight()
                        - drawable.getIntrinsicWidth()) {
                    if (passwordText.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                        //显示密码
                        passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        //隐藏密码输入框
                        passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }

                }
                return false;
            }
        });
    }


    private void initData() {
        String phone = null;
        String password = null;
        try {
            phone = this.getIntent().getStringExtra("phone");
            password = this.getIntent().getStringExtra("password");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (phone != null) {
            phoneNumber.setText(phone);
            passwordText.setText(password);
        }

    }


    /**
     * 登录
     */
    public void login() {
        final String phone = phoneNumber.getText().toString().trim();
        final String password = passwordText.getText().toString().trim();
        if (phone.length() != 11) {
            Toast.makeText(LoginActivity.this, "请输入正确手机格式", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "账户密码为空", Toast.LENGTH_SHORT).show();
            return;
        }
        savePassword(phone, password);

        final BmobQuery<Users> query = new BmobQuery<Users>();
        query.addWhereEqualTo("phone", phone);
        query.findObjects(new FindListener<Users>() {
            @Override
            public void done(List<Users> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        Users user = list.get(0);
                        if (password.equals(user.getPassword())) {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("user",user);
                            startActivity(intent);
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            Log.d("hhh", "登陆成功");
                        } else {
                            Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                            Log.d("hhh", "登陆失败");
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "您的手机号未注册过，请先注册", Toast.LENGTH_SHORT).show();
                        Log.d("hhh", "用户未注册");
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "出现错误1", Toast.LENGTH_SHORT).show();
                    //出现错误
                }
            }
        });


    }

    //将账户名和密码保存到SharedPreferences数据库中
    private void savePassword(String phone, String password) {
        //记住密码
        if (passwordRemember.isChecked()) {
            //记住密码且自动登录
            if (autoLogin.isChecked()) {
                UserManage.getInstance().saveUserInfo(LoginActivity.this, phone,
                        password, true);
            }
            //记住密码不需要自动登录
            else if (!autoLogin.isChecked()) {
                UserManage.getInstance().saveUserInfo(LoginActivity.this, phone,
                        password, false);
            }
        } else {//不需要记住密码

        }
    }

}
