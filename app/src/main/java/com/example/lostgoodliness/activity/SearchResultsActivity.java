package com.example.lostgoodliness.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.adapter.FoundSearchResultAdapter;
import com.example.lostgoodliness.adapter.LostSearchResultAdapter;
import com.example.lostgoodliness.javabean.FoundTable;
import com.example.lostgoodliness.javabean.LostTable;
import com.example.lostgoodliness.javabean.Users;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    private List<LostTable> mLostTable = new ArrayList<LostTable>();
    private List<FoundTable> mFoundTable = new ArrayList<FoundTable>();
    private Users user;
    private boolean isLost = true;
    private TextView backTv;
    private TextView sureTv;
    private TextView baseTitle;
    private boolean isMyRecord;
    //private String whoseRecord;
    private String findOrLost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);


        initData();
        initView();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.searchResult);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (!isLost) {
            FoundSearchResultAdapter adapter = new FoundSearchResultAdapter(mFoundTable,user
            ,SearchResultsActivity.this);
            recyclerView.setAdapter(adapter);
        } else if (isLost) {
            LostSearchResultAdapter adapter = new LostSearchResultAdapter(mLostTable,user,
                    SearchResultsActivity.this);
            recyclerView.setAdapter(adapter);
        }
        // LostSearchResultAdapter adapter = new LostSearchResultAdapter(mLostTable);
        //recyclerView.setAdapter(adapter);

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
