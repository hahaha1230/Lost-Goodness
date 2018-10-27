package com.example.lostgoodliness.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.activity.GoodsDetailsInfoActivity;
import com.example.lostgoodliness.javabean.FoundTable;
import com.example.lostgoodliness.javabean.LostTable;
import com.example.lostgoodliness.javabean.Users;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.client.params.ClientPNames;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 佳佳 on 10/13/2018.
 */

public class FoundSearchResultAdapter extends RecyclerView.Adapter<FoundSearchResultAdapter.ViewHolder> {
    private List<FoundTable> mFoundSearchResult;
    private Users user;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userIcon;
        TextView textView;
        TextView phoneTV;
        TextView date;

        public ViewHolder(View view) {
            super(view);
            userIcon= (CircleImageView) view.findViewById(R.id.user_icon);
            textView = (TextView) view.findViewById(R.id.resultInfo);
            phoneTV=(TextView)view.findViewById(R.id.phone);
            date=(TextView)view.findViewById(R.id.date);
        }
    }

    /**
     * 构造函数，设置初始化时候需要传入的参数
     * @param searchResults
     * @param user
     */
    public FoundSearchResultAdapter(List<FoundTable> searchResults,Users user,Context context) {
        this.mFoundSearchResult = searchResults;
        this.user=user;
        this.context=context;
    }

    @Override
    public FoundSearchResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_item_layout
                , parent, false);
        Toast.makeText(context,"共有"+mFoundSearchResult.size()+"条数据",Toast.LENGTH_SHORT).show();
        final FoundSearchResultAdapter.ViewHolder holder = new FoundSearchResultAdapter.ViewHolder(view);

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                FoundTable foundTable=mFoundSearchResult.get(position);
                Intent intent=new Intent(context, GoodsDetailsInfoActivity.class);
                intent.putExtra("type","found");
                intent.putExtra("foundInfo",foundTable);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(FoundSearchResultAdapter.ViewHolder holder, int position) {
        FoundTable foundTable=mFoundSearchResult.get(position);
        loadUserIcon(holder.userIcon,user.getUserIcon());
        String display=foundTable.getUserName();
        if (foundTable.getCity()!=null){
            display+="在"+foundTable.getCity() +"捡到了一个" +foundTable.getFindType();
        }
        else {
            display+="捡到了一个" +foundTable.getFindType();
        }
        holder.textView.setText(display);
        holder.phoneTV.setText(foundTable.getPhone());
        holder.date.setText(foundTable.getFindTime());
    }

    /**
     * 返回item count
     * @return
     */
    @Override
    public int getItemCount() {
        return mFoundSearchResult.size();
    }


    /**
     * 加载头像
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
