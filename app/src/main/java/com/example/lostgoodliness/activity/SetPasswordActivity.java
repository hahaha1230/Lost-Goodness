package com.example.lostgoodliness.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.javabean.Users;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class SetPasswordActivity extends AppCompatActivity {
    public static final int RESULT_FROM_ALBUM = 1;
    public static final int RESULT_FROM_CAMERA = 2;
    public static final int CUT_PHOTO = 3;
    private ProgressDialog progressDialog;
    private CircleImageView userIcon;
    private ImageView imageView;
    private EditText userName;
    private EditText password;
    private Button next;
    private String phone;
    private String command;
    private String userId;
    private Bitmap mBitmap;
    private String imagePath=null;       //记录选择图片的路径
    private File mFile;
    private String fileUrl;   //存储上传图片的地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        command = intent.getStringExtra("command");
        try{
            userId=intent.getStringExtra("userId");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        initConfiguration();
        initView();
    }

    private void initView() {
        userIcon=(CircleImageView)findViewById(R.id.user_icon);
        userName=(EditText)findViewById(R.id.set_user_name);
        imageView=(ImageView)findViewById(R.id.image);
        password = (EditText) findViewById(R.id.password);
        next = (Button) findViewById(R.id.next);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance()); //设置为密码输入框

        if (command.equals("resetPassword")){
            userIcon.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            userName.setVisibility(View.GONE);
        }
        else if (command.equals("register"))
        {
            userIcon.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            userName.setVisibility(View.VISIBLE);
        }

        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userIcon.showContextMenu();
            }
        });

        //创建头像的contextmenu
        userIcon.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, "拍照");
                menu.add(1, 1, 1, "从相册中选");
            }
        });

        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = password.getCompoundDrawables()[2];
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > password.getWidth() - password.getPaddingRight()
                        - drawable.getIntrinsicWidth()) {
                    if (password.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                        //显示密码
                        password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        //隐藏密码输入框
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }


                }
                return false;
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String myPassword = password.getText().toString();
                if (myPassword.isEmpty()) {
                    Toast.makeText(SetPasswordActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else if (myPassword.length() < 6) {
                    Toast.makeText(SetPasswordActivity.this, "密码长度至少6位",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                //进行重置密码
                if (command.equals("resetPassword")) {
                    final Users users = new Users();
                    users.setPassword(myPassword);
                    users.update(userId, new UpdateListener() {

                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(SetPasswordActivity.this, "更新成功："
                                        + users.getUpdatedAt(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SetPasswordActivity.this,
                                        LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SetPasswordActivity.this, "更新失败："
                                        + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else if (command.equals("register")) {
                    if (userName.getText().toString().isEmpty()){
                        Toast.makeText(SetPasswordActivity.this,"用户名不能为空",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    progressDialog.show();
                    //上传图片
                    Log.d("hhh","开始上传图片");
                    if (imagePath!=null){
                        final BmobFile bmobFile = new BmobFile(new File(imagePath));
                        bmobFile.uploadblock(new UploadFileListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    //得到上传的图片地址
                                    fileUrl = bmobFile.getFileUrl();
                                    Log.d("hhh","图片上传成功");
                                    register(myPassword,true);

                                }
                            }
                        });
                    }
                    else {
                        register(myPassword,false);
                    }
                }

            }
        });
    }


    /**
     * 进行注册
     * @param myPassword
     */
    private void register(String myPassword,boolean haveAImage){

        Log.d("hhh","实例化users类，进行保存用户信息");
        //实例化users类，进行保存用户信息
        Users users = new Users();
        users.setPhone(phone);
        users.setPassword(myPassword);
        users.setName(userName.getText().toString());
        if (haveAImage){
            users.setUserIcon(fileUrl);
        }

        //提交用户信息
        users.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    progressDialog.dismiss();
                    Toast.makeText(SetPasswordActivity.this, "注册成功：" + s,
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SetPasswordActivity.this,
                            LoginActivity.class);
                    startActivity(intent);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SetPasswordActivity.this, "注册失败：" +
                            e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("hhh", "注册失败：" + e.getErrorCode() + "----" +
                            e.getMessage() + "\n");
                }
            }
        });
    }


    /**
     * 初始化一些配置信息
     */
    private void initConfiguration() {
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        //初始化progressdialog
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("注册中...");
        progressDialog.setCancelable(true);
    }


    /**
     * 头像的context点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getGroupId()){
            case 0:
                //选择拍照
                pickImageFromCamera();
                break;
            case 1:
                //选择相册
                pickImageFromAlbum();
                break;
            default:
                break;
        }
        return true;
    }


    //拍照
    public void pickImageFromCamera(){
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!file.exists()) {
                file.mkdirs();
            }
            mFile = new File(file, System.currentTimeMillis() + ".jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, RESULT_FROM_CAMERA);
        } else {
            Toast.makeText(this, "请确认已经插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }

    //从相册获取图片
    public void pickImageFromAlbum(){
        Intent picIntent = new Intent(Intent.ACTION_PICK, null);
        picIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(picIntent, RESULT_FROM_ALBUM);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case RESULT_FROM_CAMERA:
                    startPhotoZoom(Uri.fromFile(mFile));
                    break;
                case RESULT_FROM_ALBUM:
                    if (data == null || data.getData() == null) {
                        return;
                    }
                    try {
                        //获得图片的uri
                        Uri originalUri = data.getData();
                        //这里开始的第二部分，获取图片的路径：
                        String[] proj = {MediaStore.Images.Media.DATA};
                        //好像是android多媒体数据库的封装接口，具体的看Android文档
                        Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                        //按我个人理解 这个是获得用户选择的图片的索引值
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        //将光标移至开头 ，这个很重要，不小心很容易引起越界
                        cursor.moveToFirst();
                        //最后根据索引值获取图片路径
                        imagePath = cursor.getString(column_index);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    startPhotoZoom(data.getData());
                    break;
                case CUT_PHOTO:
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;
            }
        }
    }

    /**
     * 打开系统图片裁剪功能
     *
     * @param uri  uri
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true); //黑边
        intent.putExtra("scaleUpIfNeeded", true); //黑边
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CUT_PHOTO);

    }

    /**
     * 显示图片
     * @param data
     */
    private void setPicToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {

            mBitmap = bundle.getParcelable("data");

            if(mFile != null){
                imagePath = mFile.getPath();
            }
            //显示选择的图片
            userIcon.setImageBitmap(mBitmap);
        }
    }

}
