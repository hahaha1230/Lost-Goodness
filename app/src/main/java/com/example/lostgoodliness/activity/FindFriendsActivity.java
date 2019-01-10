package com.example.lostgoodliness.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.adapter.SearchUserAdapter;
import com.example.lostgoodliness.javabean.Users;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class FindFriendsActivity extends AppCompatActivity {
    private SwipeRefreshLayout sw_refresh;
    private RecyclerView rc_view;
    private LinearLayoutManager layoutManager;
    private SearchUserAdapter adapter;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        imageLoader=ImageLoader.getInstance();
        options=new DisplayImageOptions.Builder()   // 设置图片显示相关参数
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 构建完成;

        sw_refresh=(SwipeRefreshLayout)findViewById(R.id.sw_refresh);
        rc_view=(RecyclerView)findViewById(R.id.rc_view);
        adapter = new SearchUserAdapter(FindFriendsActivity.this,imageLoader,options);
        layoutManager = new LinearLayoutManager(this);
        rc_view.setLayoutManager(layoutManager);
        rc_view.setAdapter(adapter);
        sw_refresh.setEnabled(true);
        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
            }
        });
        query();
    }


    public void query() {
        BmobQuery<Users> query = new BmobQuery<>();
        //去掉当前用户
        try {
            BmobUser user = BmobUser.getCurrentUser();
            query.addWhereNotEqualTo("username", user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //模糊查询，但是只有付费情况下才能使用模糊查询功能
        //query.addWhereContains("username", name);
        //设置最多搜索50条数据
        query.setLimit(50);
        query.order("-createdAt");
        query.findObjects(new FindListener<Users>() {
            @Override
            public void done(List<Users> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        Log.d("hhh","一共搜到"+list.size()+"条数据");
                        sw_refresh.setRefreshing(false);
                        adapter.setDatas(list);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(FindFriendsActivity.this,"没有数据哦",Toast.LENGTH_SHORT).show();
                        Log.d("hhh","没有数据哦");
                    }
                } else {
                    sw_refresh.setRefreshing(false);
                    adapter.setDatas(null);
                    adapter.notifyDataSetChanged();
                    Log.d("hhh","出现错误:"+e.getMessage());
                }
            }
        });

    }
}
