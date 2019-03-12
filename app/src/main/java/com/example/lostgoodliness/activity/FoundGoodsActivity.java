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
import com.example.lostgoodliness.javabean.FoundTable;
import com.example.lostgoodliness.javabean.Users;

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

public class FoundGoodsActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText findTime;
    private EditText findWhere;
    private EditText findType;
    private EditText goodsDescribe;
    private TextView backTv;
    private TextView sureTv;
    private TextView baseTitle;
    private TextView addImage;
    private Calendar calendar;// 用来装日期的
    private DatePickerDialog dialog;
    //用来记录自己的号码
    private String phone;
    private double latitude;
    private double longitude;
    private String city;
    private String addressInfo;
    private boolean isSaved = false;
    public static final int RESULT_FROM_MAP= 0;
    public static final int RESULT_FROM_ALBUM = 1;
    public static final int RESULT_FROM_CAMERA = 2;
    private String imagePath = null;       //记录选择图片的路径
    private File mFile;
    private Users user;
    private ImageView goodImage;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_goods);

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
        findTime = (EditText) findViewById(R.id.FindTime);
        findType = (EditText) findViewById(R.id.FindType);
        findWhere = (EditText) findViewById(R.id.FindWhere);
        goodsDescribe = (EditText) findViewById(R.id.goodsDescribe);
        addImage = (TextView) findViewById(R.id.addImage);
        goodImage=(ImageView)findViewById(R.id.good_image);
        baseTitle.setText("Found");

        backTv.setOnClickListener(this);
        sureTv.setOnClickListener(this);
        findTime.setOnClickListener(this);
        findWhere.setOnClickListener(this);
        findType.setOnClickListener(this);
        goodsDescribe.setOnClickListener(this);
        addImage.setOnClickListener(this);


        progressDialog=new ProgressDialog(FoundGoodsActivity.this);
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
            case R.id.FindTime:
                setTime();
                break;
            case R.id.FindType:
                setType();
                break;
            case R.id.FindWhere:
                Intent intent = new Intent(FoundGoodsActivity.this, GetWhereActivity.class);
                startActivityForResult(intent, RESULT_FROM_MAP);
                break;
            case R.id.addImage:
                showListDialog();
                break;
            case R.id.base_tv_right_btn:
                addInfoToTable();
                break;
        }

    }

    private void showListDialog() {
        final String[] items = {"拍照", "从相册中选"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(FoundGoodsActivity.this);
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
     * 实例化found table，并向table中添加数据
     */
    private void addInfoToTable() {
        if (findType.getText().toString().isEmpty()) {
            Toast.makeText(FoundGoodsActivity.this, "请选择物品类型", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        //向表中添加数据
        final FoundTable foundTable = new FoundTable();
        foundTable.setFindTime(findTime.getText().toString());
        foundTable.setFindType(findType.getText().toString());
        foundTable.setFindWhere(findWhere.getText().toString());
        foundTable.setGoodDescribe(goodsDescribe.getText().toString());
        foundTable.setPhone(phone);
        foundTable.setCity(city);
        foundTable.setLongitude(longitude);
        foundTable.setLatitude(latitude);
        Users users=new Users();
        users.setObjectId(user.getObjectId());
        foundTable.setLinkUsers(users);

        if (mFile != null) {
            imagePath = mFile.getPath();
        }

        if (imagePath!=null){
            final BmobFile bmobFile = new BmobFile(new File(imagePath));
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        //得到上传的图片地址
                        String fileUrl = bmobFile.getFileUrl();
                        foundTable.setFoundGoodImage(fileUrl);
                        submitAndPush(foundTable);
                    }
                }
            });
        }
        else {
            submitAndPush(foundTable);
        }
    }


    /**
     * 提交并push
     */
    private void submitAndPush(FoundTable foundTable){
        foundTable.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Toast.makeText(FoundGoodsActivity.this, "创建成功，返回的id为" + s,
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    isSaved = true;

                } else {
                    Toast.makeText(FoundGoodsActivity.this, "失败原因为：" + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    isSaved = false;
                }
            }
        });

        //推送功能
        String msg = phone + "于" + findTime.getText().toString() + "捡到了一个" + findType.getText().toString();
        BmobPushManager bmobPushManager = new BmobPushManager();
        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
        //只推送到android设备上
        query.addWhereEqualTo("deviceType", "android");
        bmobPushManager.setQuery(query);
        bmobPushManager.pushMessage(msg, new PushListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.d("hhh", "推送成功");
                    finish();
                } else {
                    Toast.makeText(FoundGoodsActivity.this, "推送失败",
                            Toast.LENGTH_SHORT).show();
                    Log.d("hhh", "推送失败");
                }
            }
        });
        if (isSaved) {
            finish();
        }
    }


    /**
     * 获取mapactivity返回回来的数据
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode)
            {
                case RESULT_FROM_MAP:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        city = bundle.getString("city");
                        addressInfo = bundle.getString("addressInfo");
                        latitude = bundle.getDouble("latitude");
                        longitude = bundle.getDouble("longitude");
                        findWhere.setText(addressInfo);
                    }
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

        } else {

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
                findTime.setText(year + "-" + month+1 + "-" + dayOfMonth);
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
                findType.setText(types[which]);
            }
        });
        builder.show();
    }
}
