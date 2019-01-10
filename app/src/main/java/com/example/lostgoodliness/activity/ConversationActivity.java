package com.example.lostgoodliness.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.adapter.ConversationAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;

public class ConversationActivity extends AppCompatActivity {
    private TextView backTv;
    private TextView sureTv;
    private RecyclerView rc_view;
    private SwipeRefreshLayout sw_refresh;
    private ConversationAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        imageLoader= ImageLoader.getInstance();
        options=new DisplayImageOptions.Builder()   // 设置图片显示相关参数
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 构建完成;

        initView();
    }

    private void initView() {
        View view = (View) findViewById(R.id.customTitle);
        backTv = (TextView) view.findViewById(R.id.base_tv_back);
        sureTv = (TextView) view.findViewById(R.id.base_tv_right_btn);
        rc_view=(RecyclerView)findViewById(R.id.rc_view);
        sw_refresh=(SwipeRefreshLayout)findViewById(R.id.sw_refresh);






        List<BmobIMConversation> conversationList= BmobIM.getInstance().loadAllConversation();

        adapter = new ConversationAdapter(ConversationActivity.this,conversationList,imageLoader,options);
        rc_view.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(ConversationActivity.this);
        rc_view.setLayoutManager(layoutManager);

        sureTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ConversationActivity.this,FindFriendsActivity.class);
                startActivity(intent);
            }
        });

        //刷新会话列表
        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                List<BmobIMConversation> conversationList= BmobIM.getInstance().loadAllConversation();
                adapter = new ConversationAdapter(ConversationActivity.this,conversationList,imageLoader,options);
                rc_view.setAdapter(adapter);
                sw_refresh.setRefreshing(false);
            }
        });

    }
}
