package com.example.lostgoodliness.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.activity.ChatActivity;
import com.example.lostgoodliness.javabean.Users;
import com.example.lostgoodliness.utils.ImageLoaderFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 佳佳 on 2019/1/1.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> {

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private List<BmobIMConversation>conversationList;
    private Context context;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public ConversationAdapter(Context context, List<BmobIMConversation>conversationList,ImageLoader
            imageLoader,DisplayImageOptions options)
    {
        this.context=context;
        this.conversationList=conversationList;
        //this.toUser=user;
        this.imageLoader=imageLoader;
        this.options=options;
    }

    @Override
    public ConversationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConversationAdapter.MyViewHolder holder = new  ConversationAdapter.MyViewHolder(LayoutInflater.
                from(context).inflate(R.layout.item_conversation, parent, false));
        return holder;
    }


    @Override
    public void onBindViewHolder(final ConversationAdapter.MyViewHolder holder, int position) {
        final BmobIMConversation conversation=conversationList.get(position);
        List<BmobIMMessage> msgs =conversation.getMessages();
        if(msgs!=null && msgs.size()>0){
            BmobIMMessage lastMsg =msgs.get(0);
            String content =lastMsg.getContent();
            if(lastMsg.getMsgType().equals(BmobIMMessageType.TEXT.getType())){
               holder.recentMsg .setText(content);
            }else if(lastMsg.getMsgType().equals(BmobIMMessageType.IMAGE.getType())){
                holder.recentMsg.setText("[图片]");
            }else if(lastMsg.getMsgType().equals(BmobIMMessageType.VOICE.getType())){
                holder.recentMsg.setText("[语音]");
            }else if(lastMsg.getMsgType().equals(BmobIMMessageType.LOCATION.getType())){
                holder.recentMsg.setText("[位置]"+content);
            }else{//开发者自定义的消息类型，需要自行处理
                holder.recentMsg.setText("[未知]");
            }


           String toUserID=null;
            holder.recentTime.setText(df.format(lastMsg.getCreateTime()));
            Log.d("hhh","current id"+BmobUser.getCurrentUser().getObjectId());
            if (BmobUser.getCurrentUser().getObjectId().equals(lastMsg.getToId())) {
                toUserID = lastMsg.getFromId();
                Log.d("hhh","from id"+lastMsg.getFromId());

            } else if (BmobUser.getCurrentUser().getObjectId().equals(lastMsg.getFromId())) {
                toUserID = lastMsg.getToId();
                Log.d("hhh","to id"+lastMsg.getToId());
            }


            BmobQuery<Users> query=new BmobQuery<>();
            query.addWhereEqualTo("objectId",toUserID);
            query.findObjects(new FindListener<Users>() {
                @Override
                public void done(List<Users> list, BmobException e) {
                    if (list.size()>0){
                        Users user=list.get(0);
                        //好友头像
                        imageLoader.displayImage( user.getUserIcon(),holder.friendIcon,options);
                       // ImageLoaderFactory.getLoader().loadAvator(holder.friendIcon,user != null ? user.getUserIcon() : null, R.mipmap.head);
                        //好友名称
                        holder.friendNickname.setText(user.getName());

                    }

                }
            });



        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("c", conversation);
                if (bundle != null) {
                    intent.putExtra(context.getPackageName(), bundle);
                }
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return conversationList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView friendIcon;
        TextView friendNickname;
        TextView recentMsg;
        TextView recentTime;

        public MyViewHolder(View view) {
            super(view);
            friendIcon = (CircleImageView) view.findViewById(R.id.friend_icon);
            friendNickname=(TextView)view.findViewById(R.id.friend_nickname);
            recentMsg=(TextView)view.findViewById(R.id.recent_msg);
            recentTime=(TextView)view.findViewById(R.id.recent_time);
        }
    }
}
