package com.example.lostgoodliness.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.javabean.LostTable;
import com.example.lostgoodliness.javabean.Users;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.util.Calendar;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class LostGoodsActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText lostTime;
    private EditText lostType;
    private EditText lostWhere;
    private EditText goodsDescribe;
    private Calendar calendar;// 用来装日期的
    private DatePickerDialog dialog;
    private String city;
    private String addressInfo;
    private TextView backTv;
    private TextView sureTv;
    private TextView baseTitle;
    private ImageView goodImage;
    private TextView addImage;
    private double latitude;
    private double longitude;
    //我的电话号码
    private String phone;
    private boolean isSaved = false;
    public static final int RESULT_FROM_ALBUM = 1;
    public static final int RESULT_FROM_CAMERA = 2;
    private String imagePath = null;       //记录选择图片的路径
    private File mFile;
    private ProgressDialog progressDialog;
    private Users user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_goods);

        //获取上个界面传过来的user信息
        try {
            user = (Users) this.getIntent().getSerializableExtra("user");
            phone = user.getPhone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initView();
    }


    /**
     * 初始化界面
     */
    private void initView() {
        View view = (View) findViewById(R.id.comment_list_title);
        backTv = (TextView) view.findViewById(R.id.base_tv_back);
        sureTv = (TextView) view.findViewById(R.id.base_tv_right_btn);
        baseTitle = (TextView) view.findViewById(R.id.base_tv_title);
        lostTime = (EditText) findViewById(R.id.LostTime);
        lostType = (EditText) findViewById(R.id.LostType);
        lostWhere = (EditText) findViewById(R.id.LostWhere);
        goodsDescribe = (EditText) findViewById(R.id.goodsDescribe);
        goodImage = (ImageView) findViewById(R.id.good_image);
        addImage = (TextView) findViewById(R.id.addImage);
        baseTitle.setText("Lost");


        backTv.setOnClickListener(this);
        sureTv.setOnClickListener(this);
        sureTv.setOnClickListener(this);
        lostWhere.setOnClickListener(this);
        lostType.setOnClickListener(this);
        lostTime.setOnClickListener(this);
        goodsDescribe.setOnClickListener(this);
        addImage.setOnClickListener(this);

        progressDialog = new ProgressDialog(LostGoodsActivity.this);
        progressDialog.setMessage("正在提交中...");
        progressDialog.setCancelable(false);
    }


    /**
     * 界面点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_tv_back:
                finish();
                break;
            case R.id.LostTime:
                setTime();
                break;
            case R.id.LostType:
                setType();
                break;
            case R.id.LostWhere:
                Intent intent = new Intent(LostGoodsActivity.this, GetWhereActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.addImage:
                showListDialog();
                break;
            case R.id.base_tv_right_btn:
                addInfoToTable();
                break;
            default:
                break;
        }
    }

    private void showListDialog() {
        final String[] items = {"拍照", "从相册中选"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(LostGoodsActivity.this);
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("拍照")) {
                    //选择拍照
                    pickImageFromCamera();
                } else {
                    //选择相册
                    pickImageFromAlbum();
                }
            }
        });
        listDialog.show();
    }


    /**
     * 拍照
     */

    private void pickImageFromCamera() {
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


    /**
     * 从相册获取图片
     */
    private void pickImageFromAlbum() {
        Intent picIntent = new Intent(Intent.ACTION_PICK, null);
        picIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(picIntent, RESULT_FROM_ALBUM);
    }


    /**
     * 实例化lost table，向表中set数据
     */
    private void addInfoToTable() {
        if (lostType.getText().toString().isEmpty()) {
            Toast.makeText(LostGoodsActivity.this, "请选择物品类型", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        final LostTable lostTable = new LostTable();
        lostTable.setLostTime(lostTime.getText().toString());
        lostTable.setLostType(lostType.getText().toString());
        lostTable.setLostWhere(lostWhere.getText().toString());
        lostTable.setGoodsDescribe(goodsDescribe.getText().toString());
        lostTable.setPhone(phone);
        lostTable.setUserName(user.getName());
        Users users = new Users();
        users.setObjectId(user.getObjectId());
        lostTable.setLinkUsers(users);
        if (latitude != 0 && longitude != 0) {
            lostTable.setLatitude(latitude);
            lostTable.setLongitude(longitude);
        }if (city != null) {
            lostTable.setCity(city);
        }if (mFile != null) {
            imagePath = mFile.getPath();
        }if (imagePath != null) {
            final BmobFile bmobFile = new BmobFile(new File(imagePath));
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    Log.d("hhh", "上传的图片成功");
                    if (e == null) {
                        //得到上传的图片地址
                        String fileUrl = bmobFile.getFileUrl();
                        lostTable.setLostGoodImage(fileUrl);
                        submitAndPush(lostTable);
                    }
                }
            });
        } else {
            submitAndPush(lostTable);
        }
    }


    /**
     * 提交并推送
     * @param lostTable
     */
    public void submitAndPush(LostTable lostTable) {
        //向服务器提交数据
        lostTable.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Toast.makeText(LostGoodsActivity.this, "创建成功，返回的id为" + s,
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    isSaved = true;
                } else {
                    Toast.makeText(LostGoodsActivity.this, "失败,原因：" + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    isSaved = false;
                }
            }
        });
        //进行push
        String msg = user.getName() + "于" + lostTime.getText().toString() + "丢失了" + lostType.getText().toString();
        BmobPushManager bmobPushManager = new BmobPushManager();
        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
        query.addWhereEqualTo("deviceType", "android");   //只推送Android设备
        bmobPushManager.setQuery(query);
        bmobPushManager.pushMessage(msg, new PushListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.d("hhh", "推送成功");
                    finish();
                } else {
                    Toast.makeText(LostGoodsActivity.this, "推送失败", Toast.LENGTH_SHORT).show();
                    Log.d("hhh", "推送失败");
                }
            }
        });
        if (isSaved) {
            finish();
        }

    }

    /**
     * 处理由map activity返回回来的数据以及拍照等返回的数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        city = bundle.getString("city");
                        addressInfo = bundle.getString("addressInfo");
                        latitude = bundle.getDouble("latitude");
                        longitude = bundle.getDouble("longitude");
                        lostWhere.setText(addressInfo);
                    }
                    break;
                case RESULT_FROM_CAMERA:
                    if (mFile != null) {
                        imagePath = mFile.getPath();
                    }
                    goodImage.setVisibility(View.VISIBLE);
                    goodImage.setImageURI(Uri.fromFile(mFile));
                    break;
                case RESULT_FROM_ALBUM:
                    if (data == null || data.getData() == null) {
                        return;
                    }
                    try {
                        Uri originalUri = data.getData();        //获得图片的uri
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mFile != null) {
                        imagePath = mFile.getPath();
                    }
                    goodImage.setVisibility(View.VISIBLE);
                    goodImage.setImageURI(data.getData());
                    break;
            }
        }
    }


    /**
     * 设置时间
     */
    private void setTime() {
        calendar = Calendar.getInstance();
        dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                lostTime.setText(year + "-" + month + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar
                .get(Calendar.MONTH), calendar
                .get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    /**
     * 设置类型
     */
    private void setType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择类型");
        //    指定下拉列表的显示数据
        final String[] types = {"苹果", "西瓜", "香蕉", "荔枝", "龙眼", "香梨"};
        //    设置一个下拉的列表选择项
        builder.setItems(types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                lostType.setText(types[which]);
            }
        });
        builder.show();
    }


}
