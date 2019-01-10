package com.example.lostgoodliness.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.activity.GoodsDetailsInfoActivity;
import com.example.lostgoodliness.javabean.FoundTable;
import com.example.lostgoodliness.javabean.Users;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 佳佳 on 10/14/2018.
 */

public class FoundRecyclerViewAdapter extends RecyclerView.Adapter<FoundRecyclerViewAdapter.MyViewHolder> {

    private List<FoundTable> mDatas;
    private Context context;
    private Users user;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private boolean isScrolling;


    public FoundRecyclerViewAdapter(Context context, List<FoundTable> mDatas, Users user, ImageLoader
            imageLoader,DisplayImageOptions options) {
        this.context = context;
        this.mDatas = mDatas;
        this.user=user;
        this.imageLoader=imageLoader;
        this.options=options;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate
                (R.layout.people_item_layout, parent, false));
        return holder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final FoundTable foundTable=mDatas.get(position);
        String display="me("+foundTable.getUserName();
        if (foundTable.getCity()!=null){
            display+=")在"+foundTable.getCity() +"捡到了一个" +foundTable.getFindType();
        }
        else {
            display+=")捡到了一个" +foundTable.getFindType();
        }

        holder.tv.setText(display);
        holder.phoneTV.setText(foundTable.getPhone());
        holder.date.setText(foundTable.getFindTime());
        String userIcon=foundTable.getLinkUsers().getUserIcon();
        Log.d("hhh","usericon is"+userIcon);
        holder.userIcon.setTag(userIcon);
       /* if(!userIcon.equals(holder.userIcon.getTag()))
        {

        }*/
        imageLoader.displayImage(userIcon,holder.userIcon,options);
       // loadUserIcon(holder.userIcon,user.getUserIcon());


        /**
         * item的点击事件
         */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, GoodsDetailsInfoActivity.class);
                intent.putExtra("type","found");
                intent.putExtra("foundInfo",foundTable);
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final String[] items = { "删除","取消" };
                final AlertDialog.Builder listDialog =
                        new AlertDialog.Builder(context);
                listDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items[which].equals("删除"))
                        {
                            FoundTable foundTable1 = new FoundTable();
                            foundTable1.delete(foundTable.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e==null){
                                        Toast.makeText(context,"删除成功",Toast.LENGTH_SHORT).show();
                                        mDatas.remove(position);
                                        notifyDataSetChanged();
                                    }
                                    else {
                                        Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
                listDialog.show();
                return true;
            }
        });

    }




    private void loadUserIcon(final CircleImageView userIcon, String userIconUrl) {


        if (userIconUrl != null) {
            ImageLoader.getInstance().loadImage(userIconUrl, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                    userIcon.setImageResource(R.mipmap.icon_fall);
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    userIcon.setImageResource(R.mipmap.home_background);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    userIcon.setImageBitmap(bitmap);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
        }
        //如果头像上传为空时候进行设置一张图片
        else {
            userIcon.setImageResource(R.mipmap.home_background);
        }

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userIcon;
        TextView phoneTV;
        TextView date;
        TextView tv;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.resultInfo);
            phoneTV=(TextView)view.findViewById(R.id.phone);
            date=(TextView)view.findViewById(R.id.date);
            userIcon=(CircleImageView)view.findViewById(R.id.user_icon);
        }
    }
}
