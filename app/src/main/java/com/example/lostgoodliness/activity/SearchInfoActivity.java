package com.example.lostgoodliness.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.example.lostgoodliness.R;
import com.example.lostgoodliness.javabean.FoundTable;
import com.example.lostgoodliness.javabean.LostTable;
import com.example.lostgoodliness.javabean.Users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SearchInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private Button searchButton;
    private EditText lostOrFind;
    private EditText goodsType;
    private EditText goodsWhere;
    private EditText searchEt;
    private EditText searchRange;
    private EditText selectTime;
    private Calendar calendar;               // 用来装日期的
    private DatePickerDialog dialog;
    private String city;
    private String addressInfoTv;
    private TextView backTv;
    private TextView sureTv;
    private TextView baseTitle;
    private double latitude;
    private double longitude;
    private double range = 0;
    private Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_info);

        //获取上个界面传过来的user信息
        try{
            user=(Users) this.getIntent().getSerializableExtra("user");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        View search = (View) findViewById(R.id.msearch);
        View view = (View) findViewById(R.id.comment_list_title);
        searchEt = (EditText) search.findViewById(R.id.et_searchtext_search);
        backTv = (TextView) view.findViewById(R.id.base_tv_back);
        sureTv = (TextView) view.findViewById(R.id.base_tv_right_btn);
        baseTitle = (TextView) view.findViewById(R.id.base_tv_title);
        lostOrFind = (EditText) findViewById(R.id.LostOrFind);
        searchButton = (Button) findViewById(R.id.searchButton);
        goodsType = (EditText) findViewById(R.id.GoodsType);
        goodsWhere = (EditText) findViewById(R.id.SelectWhere);
        searchRange = (EditText) findViewById(R.id.SearchRange);
        selectTime = (EditText) findViewById(R.id.SelectTime);
        sureTv.setVisibility(View.GONE);
        baseTitle.setText("Search");
        searchRange.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        searchButton.setOnClickListener(this);
        lostOrFind.setOnClickListener(this);
        goodsWhere.setOnClickListener(this);
        goodsType.setOnClickListener(this);
        searchRange.setOnClickListener(this);
        selectTime.setOnClickListener(this);
        backTv.setOnClickListener(this);

        searchEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable = searchEt.getCompoundDrawables()[2];
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > searchEt.getWidth() - searchEt.getPaddingRight()
                        - drawable.getIntrinsicWidth()) {
                    search();
                }

                return false;
            }
        });
    }


    /**
     * 界面点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_tv_back:                        //点击back键返回
                finish();
                break;
            case R.id.searchButton:                        //点击search进行查询
                search();
                break;
            case R.id.LostOrFind:                          //选择是lost还是found
                setLostOrFind();
                break;
            case R.id.GoodsType:                           //选择物品类型
                setType();
                break;
            case R.id.SelectWhere:                         //选择地点
                Intent intent = new Intent(SearchInfoActivity.this, GetWhereActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.SelectTime:                          //选择时间
                setTime();
                break;
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
                selectTime.setText(year + "-" + month + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar
                .get(Calendar.MONTH), calendar
                .get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }


    /**
     * 获取地图界面传回来的数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    city = bundle.getString("city");
                    addressInfoTv = bundle.getString("addressInfo");
                    latitude = bundle.getDouble("latitude");
                    longitude = bundle.getDouble("longitude");
                    goodsWhere.setText(addressInfoTv);
                }
            } else {

            }
        }
    }

    /**
     * 设置是丢失类型还是捡到类型
     */
    private void setLostOrFind() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择类型");
        //    指定下拉列表的显示数据
        final String[] types = {"丢失", "捡到"};
        //    设置一个下拉的列表选择项
        builder.setItems(types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                lostOrFind.setText(types[which]);
            }
        });
        builder.show();
    }


    /**
     * 根据所输入的参数进行查询、筛选等
     */
    private void search() {
        String type = goodsType.getText().toString();
        String where = goodsWhere.getText().toString();
        String time = selectTime.getText().toString();
        String searchrange = searchRange.getText().toString();
        String searchKeyWord = searchEt.getText().toString();

        if (searchrange!=null){
            try {
                range = Double.parseDouble(searchrange);
            } catch (Exception e) {
                Log.d("hhh", "转换成double出现异常");
                e.printStackTrace();
            }
        }

        if (lostOrFind.getText().toString().isEmpty()) {
            Toast.makeText(this, "请选择搜索丢失还是捡到", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((where.isEmpty() && (!searchrange.isEmpty()) || ((!where.isEmpty()) && searchrange.isEmpty()))) {
            Toast.makeText(SearchInfoActivity.this, "选择地点和搜索范围要么都不输入参数，" +
                    "要么都输入", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lostOrFind.getText().toString().equals("丢失")) {
            searchFromLostTable(type,time,searchKeyWord);
        }
        //从FoundTable中进行查询
        else {
            searchFromFoundTable(type,time,searchKeyWord);
        }
    }

    /**
     * 从losttable中进行查询
     * @param type
     * @param time
     * @param searchKeyWord
     */
    private void searchFromLostTable(String type,String time,String searchKeyWord) {
        BmobQuery<LostTable> query = new BmobQuery<>();
        if (!type.isEmpty()) {
            query.addWhereEqualTo("lostType", type);
        }
        if (!time.isEmpty()) {
            query.addWhereEqualTo("lostTime", time);
        }
        if (!searchKeyWord.isEmpty()) {
            Log.d("hhh","模糊查询条件为:"+searchKeyWord);
            query.addWhereContains("goodsDescribe", searchKeyWord);
        }
        else {
            Log.d("hhh","模糊查询条件为空");
        }
        query.findObjects(new FindListener<LostTable>() {
            @Override
            public void done(List<LostTable> list, BmobException e) {
                if (list.size() > 0) {
                    if (latitude != 0 && longitude != 0) {
                        List<LostTable> afterList = new ArrayList<LostTable>();
                        LatLng needLatLng = new LatLng(latitude, longitude);
                        //用给定的搜索范围进行筛选
                        for (LostTable lostTable : list) {
                            LatLng searchLatLng = new LatLng(lostTable.getLatitude(),
                                    lostTable.getLongitude());
                            double distance = AMapUtils.calculateLineDistance
                                    (needLatLng, searchLatLng);
                            //判断距离是否在给定范围内，若是，添加到筛选后的list当中
                            if (distance < range * 1000) {
                                afterList.add(lostTable);
                            }
                        }
                        //筛选过后有结果，跳转界面进行显示
                        if (afterList.size() > 0) {
                            Intent intent = new Intent(SearchInfoActivity.this,
                                    SearchResultsActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("lostTable", (Serializable) afterList);
                            intent.putExtras(bundle);
                            intent.putExtra("LostOrFound", "lost");
                            intent.putExtra("user",user);
                            startActivity(intent);
                        }
                        //筛选之后没有符合条件的结果
                        else {
                            Toast.makeText(SearchInfoActivity.this, "没有搜寻到结果",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    //用户没有添加地点，无需筛选，直接跳转显示
                    else {
                        Intent intent = new Intent(SearchInfoActivity.this,
                                SearchResultsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("lostTable", (Serializable) list);
                        intent.putExtras(bundle);
                        intent.putExtra("LostOrFound", "lost");
                        //intent.putExtra("whoseRecord","others");
                        intent.putExtra("user",user);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(SearchInfoActivity.this, "没有搜寻到结果",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 从foundtable进行查询
     * @param type
     * @param time
     * @param searchKeyWord
     */
    private void searchFromFoundTable(String type,String time,String searchKeyWord){
        BmobQuery<FoundTable> query = new BmobQuery<>();
        if (!type.isEmpty()) {
            query.addWhereEqualTo("lostType", type);
        }
        if (!time.isEmpty()) {
            query.addWhereEqualTo("lostTime", time);
        }
        if (!searchKeyWord.isEmpty()) {
            query.addWhereContains("goodsDescribe", searchKeyWord);
        }
        query.findObjects(new FindListener<FoundTable>() {
            @Override
            public void done(List<FoundTable> list, BmobException e) {
                if (list.size()>0)
                {
                    if (latitude != 0 && longitude != 0) {
                        List<FoundTable> afterList = new ArrayList<FoundTable>();
                        LatLng needLatLng = new LatLng(latitude, longitude);
                        //用给定的搜索范围进行遍历筛选
                        for (FoundTable foundTable : list) {
                            LatLng searchLatLng = new LatLng(foundTable.getLatitude(),
                                    foundTable.getLongitude());
                            //求出距离
                            double distance = AMapUtils.calculateLineDistance(needLatLng, searchLatLng);
                            if (distance < range * 1000) {
                                afterList.add(foundTable);
                            }
                        }
                        //筛选过后有结果，跳转界面进行显示
                        if (afterList.size() > 0) {
                            Intent intent = new Intent(SearchInfoActivity.this,
                                    SearchResultsActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("foundTable", (Serializable) afterList);
                            intent.putExtras(bundle);
                            intent.putExtra("LostOrFound", "found");
                            //intent.putExtra("whoseRecord","others");
                            intent.putExtra("user",user);
                            startActivity(intent);
                        }
                        //筛选之后没有符合条件的结果
                        else {
                            Toast.makeText(SearchInfoActivity.this, "没有搜寻到结果",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Intent intent = new Intent(SearchInfoActivity.this,
                                SearchResultsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("foundTable", (Serializable) list);
                        intent.putExtras(bundle);
                        intent.putExtra("LostOrFound", "found");
                        //intent.putExtra("whoseRecord","others");
                        intent.putExtra("user",user);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(SearchInfoActivity.this, "没有搜寻到结果",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * 设置丢失的类型
     */
    private void setType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择类型");
        final String[] types = {"苹果", "西瓜", "香蕉", "荔枝", "龙眼", "香梨"};
        builder.setItems(types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goodsType.setText(types[which]);
            }
        });
        builder.show();
    }
}
