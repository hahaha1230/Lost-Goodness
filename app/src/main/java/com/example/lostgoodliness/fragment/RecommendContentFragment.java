package com.example.lostgoodliness.fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.javabean.Picture;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 佳佳 on 2018/10/24.
 */

public class RecommendContentFragment extends LazyFragment {
    private ImageView recommendPicture;
    private TextView photographer;
    private TextView recommendText;
    private TextView textAuthor;
    private Picture picture;
    private int tabIndex;
    public static final  String INTENT_INT_INDEX="index";
    private ProgressDialog progressDialog;
    private ImageLoader imageLoader;
    private DisplayImageOptions displayImageOptions;



    public static RecommendContentFragment newInstance(int tabIndex,boolean isLazyLoad)
    {
        Bundle args=new Bundle();
        args.putInt(INTENT_INT_INDEX,tabIndex);
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD,isLazyLoad);
        RecommendContentFragment fragment=new RecommendContentFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.home_fragment_item);
        tabIndex=getArguments().getInt(INTENT_INT_INDEX);
        recommendPicture=(ImageView)findViewById(R.id.recommend_image);
        photographer=(TextView)findViewById(R.id.photographer);
        recommendText=(TextView)findViewById(R.id.recommend_text);
        textAuthor=(TextView)findViewById(R.id.text_author);
        imageLoader= ImageLoader.getInstance();


        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("正在加载中...");
        progressDialog.setCancelable(true);


        displayImageOptions=new DisplayImageOptions.Builder()
               // .showImageOnLoading(R.mipmap.ic_launcher) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_empty) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_error) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                //.displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 构建完成


        getData();
    }


    private void getData() {
        progressDialog.show();
        Log.d("hhh","当前界面为第"+tabIndex+"个");
        new Thread(new Runnable() {
            @Override
            public void run() {
                //异步加载数据
                BmobQuery<Picture>query=new BmobQuery<Picture>();
                query.addWhereEqualTo("PictureIndex",tabIndex);
                query.findObjects(new FindListener<Picture>() {
                    @Override
                    public void done(List<Picture> list, BmobException e) {
                        if (e==null){
                            Log.d("hhh","RecommendContentFragment加载数据成功");
                            if (list.size()>0)
                            {
                                picture=list.get(0);
                                //完成后通知主线程更新UI
                                handler.sendEmptyMessageDelayed(1,1);
                            }
                            else {
                                Log.d("hhh","没有搜到数据");
                            }


                        }
                        else {
                            Log.d("hhh","RecommendContentFragment加载数据失败");
                        }
                    }
                });


            }
        }).start();
    }



    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            progressDialog.dismiss();
            photographer.setText(picture.getPictureAuthor());
            recommendText.setText(picture.getSatement());
            textAuthor.setText(picture.getSatementAuthor());
            Log.d("hhh","获取到的图片uri为"+picture.getPicture().getFileUrl());
            //Uri uri=Uri.parse(picture.getPicture().getUrl()) ;
           // recommendPicture.setImageURI(Uri.parse(picture.getPicture().getUrl()));
            imageLoader.displayImage(picture.getPicture().getFileUrl(),recommendPicture,
                    displayImageOptions);

           // author.setText(s);
           // ivContent.setImageResource(id);
        }
    };

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        handler.removeMessages(1);
    }
}
