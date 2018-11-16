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

import com.example.lostgoodliness.adapter.LostRecyclerViewAdapter;
import com.example.lostgoodliness.javabean.Users;
import com.example.lostgoodliness.utils.DividerItemDecoration;
import com.example.lostgoodliness.Interface.MyRecyclerViewOnclickInterface;
import com.example.lostgoodliness.R;
import com.example.lostgoodliness.javabean.LostTable;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.support.v4.app.Fragment;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 佳佳 on 10/14/2018.
 */

public class LostFragment extends Fragment implements MyRecyclerViewOnclickInterface{
    private List<LostTable> lostTableList=new ArrayList<>();
    private LostRecyclerViewAdapter mAdapter;
    private SwipeRefreshLayout lostRecordRefresh;
    private RecyclerView mRecyclerview;
    public static final int UPDATE_DATA=1;
    public static final int UPDATE_TEXT=2;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ProgressDialog progressDialog;
    private TextView notFoundTV;
    private String phone;
    private Users user;
    private BmobQuery<LostTable> queryLost ;




    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lost_fragment, container, false);
        Bundle bundle = getArguments();
        phone = bundle.getString("phone");
        user=(Users) bundle.getSerializable("user");
        ButterKnife.bind(this, view);

        mRecyclerview=(RecyclerView)view.findViewById(R.id.lostFragmentRV);
        notFoundTV=(TextView)view.findViewById(R.id.notFoundTV);
        lostRecordRefresh=(SwipeRefreshLayout)view.findViewById(R.id.lostRecordRefresh);
        lostRecordRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
                // .showImageOnLoading(R.drawable.ic_stub) // 设置图片下载期间显示的图片
                // .showImageForEmptyUri(R.drawable.ic_empty) // 设置图片Uri为空或是错误的时候显示的图片
                //.showImageOnFail(R.drawable.ic_error) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                //.displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
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
     * 下拉进行刷新
     */
    private void refresh(){
        queryLost.findObjects(new FindListener<LostTable>() {
            @Override
            public void done(List<LostTable> list, BmobException e) {
                if (list.size()==0){
                   mRecyclerview.setVisibility(View.GONE);
                   Toast.makeText(getActivity(),"没有记录",Toast.LENGTH_SHORT).show();
                    lostRecordRefresh.setRefreshing(false);
                   return;
                }

                mAdapter = new LostRecyclerViewAdapter(getActivity(), lostTableList,user,imageLoader,options);
                //设置布局管理器
                LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
                mRecyclerview.setLayoutManager(layoutManager);
                //设置adapter
                mRecyclerview.setAdapter(mAdapter);
                //添加分割线
                mRecyclerview.addItemDecoration(new DividerItemDecoration(getActivity(),
                        DividerItemDecoration.VERTICAL_LIST));
                Toast.makeText(getContext(),"刷新成功，共"+lostTableList.size()+"条数据",
                        Toast.LENGTH_SHORT).show();
                lostRecordRefresh.setRefreshing(false);
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
                queryLost.findObjects(new FindListener<LostTable>() {
                    @Override
                    public void done(List<LostTable> list, BmobException e) {
                        if (list.size()>0){
                        lostTableList=list;
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
                    mAdapter = new LostRecyclerViewAdapter(getActivity(), lostTableList,user,imageLoader,options);
                    //设置布局管理器
                    LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
                    mRecyclerview.setLayoutManager(layoutManager);
                    //设置adapter
                    mRecyclerview.setAdapter(mAdapter);
                    //添加分割线
                    mRecyclerview.addItemDecoration(new DividerItemDecoration(getActivity(),
                            DividerItemDecoration.VERTICAL_LIST));
                   // mAdapter.setOnItemClickLitener(getActivity());
                    break;
                case UPDATE_TEXT:
                    Log.d("hhh","没有获取到数据");
                    Toast.makeText(getActivity(),"没有数据哦",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    break;
                    default:
                        break;
            }
        }
    };


    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getActivity(), lostTableList.get(position).getPhone(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Toast.makeText(getActivity(), "onItemLongClick" + lostTableList.get(position), Toast.LENGTH_SHORT).show();
    }
}
