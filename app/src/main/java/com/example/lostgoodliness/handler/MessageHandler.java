package com.example.lostgoodliness.handler;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.example.lostgoodliness.activity.ChatActivity;
import com.example.lostgoodliness.adapter.ChatAdapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by 佳佳 on 2018/12/30.
 */

public class MessageHandler extends BmobIMMessageHandler {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String TAG = "hhh";

    @Override
    public void onMessageReceive(MessageEvent event) {
        //收到在线消息，先判断消息来源是否是正在聊天的对象，若是，则通知adapter添加数据，否则不予处理
       if (event.getMessage().getFromId().equals(ChatActivity.toUserID))
       {
           ChatActivity.adapter.addMessage(event.getMessage());  //通知adapter添加message
           ChatActivity.layoutManager.scrollToPositionWithOffset(ChatActivity.adapter.getItemCount() - 1, 0);
       }
    }

    @Override
    public void onOfflineReceive(OfflineMessageEvent event) {
        super.onOfflineReceive(event);
        //离线消息，每次connect的时候会查询离线消息，如果有，此方法会被调用
        Map<String, List<MessageEvent>> map = event.getEventMap();
        //挨个检测下离线消息所属的用户的信息是否需要更新
        Log.d("hhh","有" + map.size() + "个用户发来离线消息");
       // Toast.makeText(MainActivity.context, , Toast.LENGTH_SHORT).show();
        for (Map.Entry<String, List<MessageEvent>> entry : map.entrySet()) {
            List<MessageEvent> list = entry.getValue();
            int size = list.size();
            Log.e(TAG, "onOfflineReceive: "+"用户" + entry.getKey() + "发来" + size + "条消息" );
            for (int i = 0; i < size; i++) {
                Log.e(TAG, "bindView:  离线消息： "+i+ "  getFromId "+list.get(i).getMessage().getFromId() );
                Log.e(TAG, "bindView:  离线消息： "+i+ "  getContent "+list.get(i).getMessage().getContent() );
                Log.e(TAG, "bindView:  离线消息： "+i+ "  getExtra "+list.get(i).getMessage().getExtra() );
                Log.e(TAG, "bindView:  离线消息： "+i+ "  getToId "+list.get(i).getMessage().getToId() );
                Log.e(TAG, "bindView:  离线消息： "+i+ "  getCreateTime "+df.format(list.get(i).getMessage().getCreateTime()));
                Log.e(TAG, "bindView:  离线消息： "+i+ "  getReceiveStatus "+list.get(i).getMessage().getReceiveStatus() );
            }
        }


    }



}
