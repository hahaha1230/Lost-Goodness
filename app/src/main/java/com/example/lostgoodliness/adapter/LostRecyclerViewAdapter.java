package com.example.lostgoodliness.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.example.lostgoodliness.Interface.MyRecyclerViewOnclickInterface;
import com.example.lostgoodliness.R;
import com.example.lostgoodliness.activity.GoodsDetailsInfoActivity;
import com.example.lostgoodliness.javabean.FoundTable;
import com.example.lostgoodliness.javabean.LostTable;
import com.example.lostgoodliness.javabean.Users;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 佳佳 on 10/14/2018.
 */

public class LostRecyclerViewAdapter extends RecyclerView.Adapter<LostRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private List<LostTable> mDatas;
    private Users user;

    private MyRecyclerViewOnclickInterface mOnItemClickLitener;

    public void setOnItemClickLitener(MyRecyclerViewOnclickInterface mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public LostRecyclerViewAdapter(Context context, List<LostTable> mDatas, Users users) {
        this.context = context;
        this.mDatas = mDatas;
        this.user=users;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate
                (R.layout.people_item_layout, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final LostTable lostTable=mDatas.get(position);
        String display="me("+lostTable.getUserName();
        if (lostTable!=null)
        {
            display+=")在"+lostTable.getCity()+"丢失了一个" + lostTable.getLostType();
        }
        else {
            display+=")丢失了一个" + lostTable.getLostType();
        }
        holder.tv.setText(display );
        holder.phoneTV.setText(lostTable.getPhone());
        holder.date.setText(lostTable.getLostTime());
        loadUserIcon(holder.userIcon,user.getUserIcon());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, GoodsDetailsInfoActivity.class);
                intent.putExtra("type","lost");
                intent.putExtra("lostInfo",lostTable);
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
                            LostTable lostTable1=new LostTable();
                            lostTable1.delete(lostTable.getObjectId(), new UpdateListener() {
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
                        else {

                        }

                    }
                });
                listDialog.show();
                return true;
            }
        });
        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            //点击监听
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });

            //长按监听
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                    //返回true可以让长按事件被消耗，避免出发点击事件
                    return true;
                }
            });
        }
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
        TextView tv;
        TextView date;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.resultInfo);
            userIcon=(CircleImageView)view.findViewById(R.id.user_icon);
            phoneTV=(TextView)view.findViewById(R.id.phone);
            date=(TextView)view.findViewById(R.id.date);
        }
    }
}