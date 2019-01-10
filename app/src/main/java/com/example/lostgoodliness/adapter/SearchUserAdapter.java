package com.example.lostgoodliness.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.activity.ChatActivity;
import com.example.lostgoodliness.javabean.Users;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author :smile
 * @project:SearchUserAdapter
 * @date :2016-01-22-14:18
 */
public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder>{
    private List<Users> users = new ArrayList<>();
    private Context context;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public SearchUserAdapter() {

    }
    public SearchUserAdapter(Context context,ImageLoader imageLoader,DisplayImageOptions options) {
        this.context=context;
        this.imageLoader=imageLoader;
        this.options=options;
    }


    public void setDatas(List<Users> list) {
        users.clear();
        if (null != list) {
            users.addAll(list);
        }
    }

    /**获取用户
     * @param position
     * @return
     */
    public Users getItem(int position){
        return users.get(position);
    }


    @Override
    public SearchUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SearchUserAdapter.ViewHolder holder=new SearchUserAdapter.ViewHolder(LayoutInflater.
                from(context).inflate(R.layout.item_search_user, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final SearchUserAdapter.ViewHolder holder, final int position) {
       final Users user =users.get(position);
        holder.user_name.setText(user.getName());
        String userIcon=user.getUserIcon();
        holder.user_icon.setTag(userIcon);
        imageLoader.displayImage(userIcon,holder.user_icon,options);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getUserIcon());
                //TODO 会话：4.1、创建一个常态会话入口，好友聊天
                BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, null);
                Bundle bundle = new Bundle();
                bundle.putSerializable("c", conversationEntrance);
                Intent intent = new Intent();
                intent.setClass(context, ChatActivity.class);
                intent.putExtra("user",user.getObjectId());
                if (bundle != null) {
                    intent.putExtra(context.getPackageName(), bundle);
                }
               context.startActivity(intent);


            }
        });
       /* if (userIcon != null) {
            ImageLoader.getInstance().loadImage(userIcon, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    holder.user_icon.setImageResource(R.mipmap.home_background);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    holder.user_icon.setImageBitmap(bitmap);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
        }

        //如果头像上传为空时候进行设置一张图片
        else {
           // userIcon.setImageResource(R.mipmap.home_background);
        }*/
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
         CircleImageView user_icon;
        TextView user_name;
        public ViewHolder(View view) {
            super(view);
           user_icon=(CircleImageView)view.findViewById(R.id.user_icon);
            user_name=(TextView)view.findViewById(R.id.user_name);
        }
    }
}
