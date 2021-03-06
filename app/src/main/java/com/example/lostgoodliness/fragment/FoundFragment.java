package com.example.lostgoodliness.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostgoodliness.adapter.FoundRecyclerViewAdapter;
import com.example.lostgoodliness.javabean.Users;
import com.example.lostgoodliness.utils.DividerItemDecoration;
import com.example.lostgoodliness.Interface.OnRecyclerViewListener;
import com.example.lostgoodliness.javabean.FoundTable;
import com.example.lostgoodliness.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 佳佳 on 10/14/2018.
 */

public class FoundFragment extends Fragment  {
    //@BindView(R.id.foundFragmentRV)
    public static final int UPDATE_DATA=1;      //获取到数据时候通知主线程加载数据
    public static final int UPDATE_TEXT=2;      //未获取到数据时候通知主线程更改界面
    private RecyclerView mRecyclerview;
    private FoundRecyclerViewAdapter mAdapter;
    private List<FoundTable> foundTableList;
    private SwipeRefreshLayout foundRecordRefresh;
    private ProgressDialog progressDialog;
    private TextView notFoundTV;
    private String phone;                       //存储手机号
    private Users user;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private BmobQuery<FoundTable> queryLost;



    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.found_fragment, container, false);
        Bundle bundle = getArguments();
        phone = bundle.getString("phone");
        user=(Users) bundle.getSerializable("user");
        ButterKnife.bind(this, view);

        notFoundTV=(TextView) view.findViewById(R.id.notFoundTV);
        mRecyclerview=(RecyclerView)view.findViewById(R.id.foundFragmentRV);
        foundRecordRefresh=(SwipeRefreshLayout)view.findViewById(R.id.foundRecordRefresh);
        foundRecordRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        initConfiguration();

        initData();

        return view;
    }

    /**
     * 初始化一些配置
     */
    private void initConfiguration() {
        imageLoader=ImageLoader.getInstance();
        options=new DisplayImageOptions.Builder()   // 设置图片显示相关参数
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 构建完成;

        //初始化progressdialog
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("数据正在加载中...");
        progressDialog.setCancelable(true);

        queryLost = new BmobQuery<>();
        queryLost.addWhereEqualTo("phone", phone);
        queryLost.include("linkUsers");
    }


    /**
     * 刷新界面
     */
    private void refresh(){
        queryLost.findObjects(new FindListener<FoundTable>() {
            @Override
            public void done(List<FoundTable> list, BmobException e) {
                if (list.size()==0){
                    mRecyclerview.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),"没有记录哦",Toast.LENGTH_SHORT).show();
                    foundRecordRefresh.setRefreshing(false);
                    return;
                }

                mAdapter = new FoundRecyclerViewAdapter(getActivity(), foundTableList,user,imageLoader,options);
                //设置布局管理器
                LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
                mRecyclerview.setLayoutManager(layoutManager);
                //设置adapter
                mRecyclerview.setAdapter(mAdapter);
                //添加分割线
                mRecyclerview.addItemDecoration(new DividerItemDecoration(getActivity(),
                        DividerItemDecoration.VERTICAL_LIST));
                Toast.makeText(getContext(),"刷新成功，共"+foundTableList.size()+"条数据",
                        Toast.LENGTH_SHORT).show();
                foundRecordRefresh.setRefreshing(false);
                notFoundTV.setVisibility(View.GONE);
            }
        });
    }


    /**
     * 开启子线程请求网络数据
     */
    private void initData() {
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                queryLost.findObjects(new FindListener<FoundTable>() {
                    @Override
                    public void done(List<FoundTable> list, BmobException e) {
                        if (list.size()>0){
                            foundTableList=list;
                            //获取到数据后通知主线程更新数据
                            Message message=new Message();
                            message.what=UPDATE_DATA;
                            handler.sendMessage(message);
                        }
                        else {
                            //没有获取到数据也通知主线程更新界面
                            Message message=new Message();
                            message.what=UPDATE_TEXT;
                            handler.sendMessage(message);
                        }
                    }
                });

            }
        }).start();
    }


    /**
     * 子线程获取到数据后在这里进行更新界面
     */
    private Handler handler=new Handler(){
        public void  handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_DATA:
                    progressDialog.dismiss();
                    mAdapter = new FoundRecyclerViewAdapter(getActivity(), foundTableList,user,imageLoader,options);
                    //设置布局管理器
                    LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
                    mRecyclerview.setLayoutManager(layoutManager);
                    //设置adapter
                    mRecyclerview.setAdapter(mAdapter);
                    //添加分割线
                    mRecyclerview.addItemDecoration(new DividerItemDecoration(getActivity(),
                            DividerItemDecoration.VERTICAL_LIST));
                    //mAdapter.setOnItemClickLitener(getActivity());
                    break;
                case UPDATE_TEXT:
                    Toast.makeText(getActivity(),"没有数据哦",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

}
