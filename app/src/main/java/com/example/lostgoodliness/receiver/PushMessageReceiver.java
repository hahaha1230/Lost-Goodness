package com.example.lostgoodliness.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.lostgoodliness.activity.GoodsDetailsInfoActivity;
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
    private NotificationManager notificationManager;
    private String message="";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            //如果用户取消接收推送信息，接收到信息后不需要解析，也不需要通知用户
            if (!HomeActivity.isReceivePush){
                return;
            }
            //对message进行简单解析
            String msg= intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            Log.d("hhh","msg:"+msg);
            JSONTokener jsonTokener=new JSONTokener(msg);
            try {
                JSONObject object=(JSONObject)jsonTokener.nextValue();
                message=object.getString("alert");
            }
            catch (JSONException e){
                e.printStackTrace();
            }
           // notificationManager=(NotificationManager)
            notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
           // showNotification(context, message);
           PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, GoodsDetailsInfoActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
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
                        .setChannelId(id)//无效
                        .setContentIntent(pendingIntent);

                notification = notificationBuilder.build();
            }
           manager.notify(111123, notification);

           /* NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("标题");
            builder.setContentText(message);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentIntent(pendingIntent);
            Notification notification = builder.build();
            NotificationManager manager =(NotificationManager)context.getSystemService
                    (Context.NOTIFICATION_SERVICE);
            manager.notify(1,notification);*/
        }
    }

    private void showNotification(Context context,String message) {
        Notification.Builder builder = new Notification.Builder(context);
        Intent mIntent = new Intent(context,GoodsDetailsInfoActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.found_icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));
        builder.setAutoCancel(true);
        builder.setContentTitle(message);
       // selectNotofovatiomLevel(builder);
       // builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        builder.setContentText("public");
        //用RemoteViews来创建自定义Notification视图
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.view_fold);
        Notification notification = builder.build();
        //指定展开时的视图
        notification.bigContentView = remoteViews;
        notificationManager.notify(1, notification);
    }
}
