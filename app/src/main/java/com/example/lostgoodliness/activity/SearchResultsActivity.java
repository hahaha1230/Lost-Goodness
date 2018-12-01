package com.example.lostgoodliness.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.adapter.FoundSearchResultAdapter;
import com.example.lostgoodliness.adapter.LostSearchResultAdapter;
import com.example.lostgoodliness.javabean.FoundTable;
import com.example.lostgoodliness.javabean.LostTable;
import com.example.lostgoodliness.javabean.Users;
import com.example.lostgoodliness.utils.DividerItemDecoration;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    private List<LostTable> mLostTable = new ArrayList<LostTable>();
    private List<FoundTable> mFoundTable = new ArrayList<FoundTable>();
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private TextView backTv;
    private TextView sureTv;
    private TextView baseTitle;
    private String findOrLost;
    private boolean isLost = true;
    private Users user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        imageLoader=ImageLoader.getInstance();
        options=new DisplayImageOptions.Builder()   // 设置图片显示相关参数
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 构建完成;


        initData();
        initView();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.searchResult);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        if (!isLost) {
            final FoundSearchResultAdapter adapter = new FoundSearchResultAdapter(mFoundTable
            ,SearchResultsActivity.this,imageLoader,options);
            adapter.setHasStableIds(true);
            recyclerView.addItemDecoration(new DividerItemDecoration(this,
                    DividerItemDecoration.VERTICAL_LIST));
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    // State有三种状态：SCROLL_STATE_IDLE（静止）、SCROLL_STATE_DRAGGING（上升）、
                    // SCROLL_STATE_SETTLING（下落）
                    if (newState ==0) { // 滚动静止时才加载图片资源，极大提升流畅度
                        adapter.setScrolling(false);
                        adapter.notifyDataSetChanged(); // notify调用后onBindViewHolder会响应调用
                    } else
                    {
                        adapter.setScrolling(true);
                    }

                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
            recyclerView.setAdapter(adapter);
        } else if (isLost) {
            final LostSearchResultAdapter adapter = new LostSearchResultAdapter(mLostTable,
                    SearchResultsActivity.this,imageLoader,options);
            adapter.setHasStableIds(true);
            recyclerView.addItemDecoration(new DividerItemDecoration(this,
                    DividerItemDecoration.VERTICAL_LIST));
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState ==0) { // 滚动静止时才加载图片资源，极大提升流畅度
                       adapter.setScrolling(false);
                        adapter.notifyDataSetChanged(); // notify调用后onBindViewHolder会响应调用
                    } else
                    {
                        adapter.setScrolling(true);
                    }
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }


    /**
     * 初始化界面
     */
    private void initView() {
        View view = (View) findViewById(R.id.comment_list_title);
        backTv = (TextView) view.findViewById(R.id.base_tv_back);
        sureTv = (TextView) view.findViewById(R.id.base_tv_right_btn);
        baseTitle = (TextView) view.findViewById(R.id.base_tv_title);

        baseTitle.setText("Search Results");
        sureTv.setVisibility(View.GONE);
        backTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    /**
     * 初始化数据
     */
    private void initData() {
        user=(Users) this.getIntent().getSerializableExtra("user");
        findOrLost = this.getIntent().getStringExtra("LostOrFound");

        if (findOrLost.equals("lost")) {
            isLost = true;
            mLostTable = (List<LostTable>) this.getIntent().getSerializableExtra("lostTable");
        } else {
            isLost = false;
            mFoundTable = (List<FoundTable>) this.getIntent().getSerializableExtra("foundTable");
        }

    }
}
