package com.example.lostgoodliness.activity;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.fragment.FoundFragment;
import com.example.lostgoodliness.fragment.LostFragment;
import com.example.lostgoodliness.javabean.LostTable;
import com.example.lostgoodliness.javabean.Users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyIssueRecordActivity extends AppCompatActivity  implements View.OnClickListener{
    private  List<Fragment>mFragmentList=new ArrayList<Fragment>();
    private ArrayList<String>lostDatas=new ArrayList<>();
    private LostFragment lostFragment;
    private FoundFragment foundFragment;
    private FragmentAdapter mFragmentAdapter;
    private static final int UPDATE_TEXT=1;
    private TextView title;
    private TextView lostItem;
    private TextView foundItem;
    private TextView backTv;
    private TextView baseTitle;
    private TextView sureTv;
    private ViewPager vp;
    private String phone;
    private Users user;

   private String[] titles = new String[]{"Lost", "Found"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_issue_record);

        user=(Users) this.getIntent().getSerializableExtra("user");
        phone=user.getPhone();
        getRight();


        initView();
       // initData();
        mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), mFragmentList);
        vp.setOffscreenPageLimit(2);//ViewPager的缓存为2帧
        vp.setAdapter(mFragmentAdapter);
        vp.setCurrentItem(0);//初始设置ViewPager选中第一帧
        lostItem.setTextColor(Color.parseColor("#66CDAA"));


        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                baseTitle.setText(titles[position]);
                changeTextColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private void getRight(){
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            //TODO
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d("hhh","没得权限");
                }
                else {
                    Log.d("hhh","有权限");
                }
                break;
                default:
                    break;
        }
    }


    /**
     * 初始化界面
     */
    private void initView() {
        View view = (View) findViewById(R.id.customTitle);
        backTv = (TextView) view.findViewById(R.id.base_tv_back);
        sureTv = (TextView) view.findViewById(R.id.base_tv_right_btn);
        baseTitle=(TextView)view.findViewById(R.id.base_tv_title);
        title=(TextView)findViewById(R.id.title);
        lostItem=(TextView)findViewById(R.id.item_lost);
        foundItem=(TextView)findViewById(R.id.item_found);
        vp = (ViewPager) findViewById(R.id.mainViewPager);

        lostItem.setOnClickListener(this);
        foundItem.setOnClickListener(this);
        backTv.setOnClickListener(this);
        sureTv.setVisibility(View.GONE);
        baseTitle.setText("My");


        lostFragment=new LostFragment();
        Bundle bundleLost = new Bundle();
        bundleLost.putString("phone",phone);
        bundleLost.putSerializable("user",user);
        lostFragment.setArguments(bundleLost);
        foundFragment=new FoundFragment();
        Bundle bundleFound = new Bundle();
        bundleFound.putString("phone",phone);
        bundleFound.putSerializable("user",user);
        foundFragment.setArguments(bundleFound);


        //给fragment添加数据
        mFragmentList.add(lostFragment);
        mFragmentList.add(foundFragment);
    }

    /**
     * 界面点击事件
     * @param v
     */

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.item_lost:
                vp.setCurrentItem(0, true);
                break;
            case R.id.item_found:
                vp.setCurrentItem(1, true);
                break;
            case R.id.base_tv_back:
                finish();
                break;
                default:
                    break;
        }
    }


    /**
     * fragment 的adapter
     */
    public class FragmentAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<Fragment>();

        public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }


        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }

    /**
     *由ViewPager的滑动修改底部导航Text的颜色
     */
    private void changeTextColor(int position) {
        if (position == 0) {
            lostItem.setTextColor(Color.parseColor("#66CDAA"));
            foundItem.setTextColor(Color.parseColor("#000000"));

        } else if (position == 1) {
            foundItem.setTextColor(Color.parseColor("#66CDAA"));
            lostItem.setTextColor(Color.parseColor("#000000"));
        }
    }
}
