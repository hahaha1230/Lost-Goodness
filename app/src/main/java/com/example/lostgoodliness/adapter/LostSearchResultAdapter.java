package com.example.lostgoodliness.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.activity.GoodsDetailsInfoActivity;
import com.example.lostgoodliness.javabean.LostTable;
import com.example.lostgoodliness.javabean.Users;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 佳佳 on 10/11/2018.
 */

public class LostSearchResultAdapter extends RecyclerView.Adapter<LostSearchResultAdapter.ViewHolder> {

    private List<LostTable> mLostSearchResult;
    private Users user;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View infoView;
        CircleImageView userImage;
        TextView textView;
        TextView phoneTV;
        TextView date;


        public ViewHolder(View view) {
            super(view);
            infoView=view;
            userImage = (CircleImageView) view.findViewById(R.id.user_icon);
            textView = (TextView) view.findViewById(R.id.resultInfo);
            phoneTV=(TextView)view.findViewById(R.id.phone);
            date=(TextView)view.findViewById(R.id.date);

        }

    }

    public LostSearchResultAdapter(List<LostTable> searchResults, Users user,Context context) {
        this.mLostSearchResult = searchResults;
        this.user = user;
        this.context=context;
    }

    @Override
    public LostSearchResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_item_layout
                , parent, false);

        Toast.makeText(context,"共有"+mLostSearchResult.size()+"条数据",Toast.LENGTH_SHORT).show();
        final ViewHolder holder = new ViewHolder(view);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                LostTable lostTable=mLostSearchResult.get(position);
                Intent intent=new Intent(context, GoodsDetailsInfoActivity.class);
                intent.putExtra("type","lost");
                intent.putExtra("lostInfo",lostTable);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(LostSearchResultAdapter.ViewHolder holder, int position) {
        LostTable lostTable = mLostSearchResult.get(position);
        loadUserIcon(holder.userImage, user.getUserIcon());
        String display=lostTable.getUserName();
        if (lostTable!=null)
        {
            display+="在"+lostTable.getCity()+"丢失了一个" + lostTable.getLostType();
        }
        else {
            display+="丢失了一个" + lostTable.getLostType();
        }
        holder.textView.setText(display);
        holder.phoneTV.setText(lostTable.getPhone());
        holder.date.setText(lostTable.getLostTime());
    }

    @Override
    public int getItemCount() {
        return mLostSearchResult.size();
    }


    /**
     * 设置头像
     * @param userIcon
     * @param userIconUrl
     */
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
}
