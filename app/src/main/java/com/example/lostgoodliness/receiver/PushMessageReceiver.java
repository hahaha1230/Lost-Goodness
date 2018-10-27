package com.example.lostgoodliness.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.lostgoodliness.activity.HomeActivity;
import com.example.lostgoodliness.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import cn.bmob.push.PushConstants;

/**
 * Created by 佳佳 on 10/17/2018.
 * 接收推送
 */

public class PushMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message="";
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            //如果用户取消接收推送信息，接收到信息后不需要解析，也不需要通知用户
            if (!HomeActivity.isReceivePush){
                return;
            }
            String msg= intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            JSONTokener jsonTokener=new JSONTokener(msg);
            try {
                JSONObject object=(JSONObject)jsonTokener.nextValue();
                message=object.getString("alert");
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            NotificationManager manager=(NotificationManager) context.getSystemService
                    (Context.NOTIFICATION_SERVICE);
            Notification notification = null;
            String id = "my_channel_01";
            String name="渠道1";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(id, name,
                        NotificationManager.IMPORTANCE_LOW);
                manager.createNotificationChannel(mChannel);
                notification = new Notification.Builder(context)
                        .setChannelId(id)
                        .setContentTitle("新消息")
                        .setContentText(message)
                        .setSmallIcon(R.mipmap.ic_launcher).build();
            } else {
               NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                        .setContentTitle("新消息")
                        .setContentText(message)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setOngoing(true)
                        .setChannelId(id);//无效
                notification = notificationBuilder.build();
            }
           manager.notify(111123, notification);
        }
    }
}
