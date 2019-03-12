package com.example.lostgoodliness.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.adapter.ChatAdapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

import com.example.lostgoodliness.Interface.OnRecyclerViewListener;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private BmobIMConversation mConversationManager;
    private Button send;
    private EditText edit_msg;
    public static LinearLayoutManager layoutManager;
    private RecyclerView rc_view;
    private SwipeRefreshLayout sw_refresh;
    public static ChatAdapter adapter;
    private LinearLayout layout_emo;
    private LinearLayout layout_more;
    private LinearLayout layout_add;
    private Button btn_chat_keyboard;
    private Button btn_chat_voice;
    private Button btn_speak;
    private Button btn_chat_add;
    private LinearLayout ll_chat;
    public static String toUserID;           //用于记录当前正在聊天的USER ID
    private TextView tv_picture;
    private final int CHOOSE_PHOTO = 1;
    private final  int CHOOSE_CAMERA=2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        try {
            BmobIMConversation conversationEntrance = (BmobIMConversation) getIntent().
                    getBundleExtra(getPackageName()).getSerializable("c");
            //初始化聊天管理员
            mConversationManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);

            Log.d("hhh", "获取conversation成功");
            try {
                Log.d("hhh", "111");
                BmobIMMessage lastMsg = conversationEntrance.getMessages().get(0);
                Log.d("hhh", "222");
                if (BmobUser.getCurrentUser().getObjectId().equals(lastMsg.getToId())) {
                    Log.d("hhh", "333");
                    toUserID = lastMsg.getFromId();
                } else if (BmobUser.getCurrentUser().getObjectId().equals(lastMsg.getFromId())) {
                    Log.d("hhh", "444");
                    toUserID = lastMsg.getToId();
                }
                Log.d("to id ", toUserID);
            } catch (Exception e) {
                Log.d("hhh", "获取id失败");
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        initview();
        initconfig();
    }


    private void initconfig() {
        sw_refresh.setEnabled(true);
        layoutManager = new LinearLayoutManager(this);
        rc_view.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(this, mConversationManager);
        rc_view.setAdapter(adapter);


        ll_chat.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ll_chat.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                sw_refresh.setRefreshing(true);
                //自动刷新
                queryMessages(null);
            }
        });

        //下拉加载
        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BmobIMMessage msg = adapter.getFirstMessage();
                queryMessages(msg);
            }
        });


        //设置RecyclerView的点击事件
        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("hhh", "position" + position);
            }

            @Override
            public boolean onItemLongClick(int position) {
                //TODO 消息：5.3、删除指定聊天消息
                mConversationManager.deleteMessage(adapter.getItem(position));
                adapter.remove(position);
                return true;
            }
        });
    }

    private TextView tv_camera;

    private void initview() {
        send = (Button) findViewById(R.id.btn_chat_send);
        edit_msg = (EditText) findViewById(R.id.edit_msg);
        rc_view = (RecyclerView) findViewById(R.id.rc_view);
        sw_refresh = (SwipeRefreshLayout) findViewById(R.id.sw_refresh);
        layout_emo = (LinearLayout) findViewById(R.id.layout_emo);
        layout_more = (LinearLayout) findViewById(R.id.layout_more);
        layout_add = (LinearLayout) findViewById(R.id.layout_add);
        btn_chat_keyboard = (Button) findViewById(R.id.btn_chat_keyboard);
        btn_chat_voice = (Button) findViewById(R.id.btn_chat_voice);
        btn_speak = (Button) findViewById(R.id.btn_speak);
        ll_chat = (LinearLayout) findViewById(R.id.ll_chat);
        btn_chat_add = (Button) findViewById(R.id.btn_chat_add);
        tv_picture = (TextView) findViewById(R.id.tv_picture);
        tv_camera=(TextView)findViewById(R.id.tv_camera);


        tv_camera.setOnClickListener(this);
        tv_picture.setOnClickListener(this);
        btn_chat_add.setOnClickListener(this);
        send.setOnClickListener(this);
        edit_msg.setOnClickListener(this);


        edit_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                send.setVisibility(View.VISIBLE);
                btn_chat_keyboard.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.GONE);
                btn_speak.setVisibility(View.GONE);

            }
        });
    }

    /**
     * 首次加载，可设置msg为null，下拉刷新的时候，默认取消息表的第一个msg作为刷新的起始时间点，默认按照消息时间的降序排列
     * @param msg
     */
    public void queryMessages(BmobIMMessage msg) {
        mConversationManager.queryMessages(msg, 10, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                sw_refresh.setRefreshing(false);
                if (e == null) {
                    if (null != list && list.size() > 0) {
                        adapter.addMessages(list);
                        layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                    }
                } else {
                    Toast.makeText(ChatActivity.this, e.getMessage() + "(" + e.getErrorCode() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_chat_send:
                if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
                    Toast.makeText(this, "尚未连接IM服务器", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage();
                break;
            case R.id.edit_msg:
                if (layout_more.getVisibility() == View.VISIBLE) {
                    layout_add.setVisibility(View.GONE);
                    layout_emo.setVisibility(View.GONE);
                    layout_more.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_chat_add:
                if (layout_more.getVisibility() == View.GONE) {
                    layout_more.setVisibility(View.VISIBLE);
                    layout_add.setVisibility(View.VISIBLE);
                    layout_emo.setVisibility(View.GONE);
                    hideSoftInputView();
                } else {
                    if (layout_emo.getVisibility() == View.VISIBLE) {
                        layout_emo.setVisibility(View.GONE);
                        layout_add.setVisibility(View.VISIBLE);
                    } else {
                        layout_more.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.tv_picture:
                if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
                    Toast.makeText(this, "尚未连接IM服务器", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendLocalImageMessage();
                break;
            case R.id.tv_camera:
                if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
                    Toast.makeText(this, "尚未连接IM服务器", Toast.LENGTH_SHORT).show();
                    return;
                }
                pickImageFromCamera();
                break;

        }
    }

    private  File mFile;

    //拍照
    public void pickImageFromCamera(){
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!file.exists()) {
                file.mkdirs();
            }
             mFile = new File(file, System.currentTimeMillis() + ".jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent,CHOOSE_CAMERA);
        } else {
            Toast.makeText(this, "请确认已经插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 发送本地图片文件
     */
    public void sendLocalImageMessage() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case CHOOSE_PHOTO:
              //  if (requestCode == RESULT_OK) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用该方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        //4.4以下系统使用该方法处理图片
                        handleImageBeforeKitKat(data);
                    }
               // }
                break;
            case CHOOSE_CAMERA:
                BmobIMImageMessage image = new BmobIMImageMessage(mFile.getAbsolutePath());
                mConversationManager.sendMessage(image, listener);
                break;
            default:
                break;
        }
    }


    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri,则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.document".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selection);
            } else if ("com.android.providers.downloads.documents".equals
                    (uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse(
                        "content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的Uri，则使用普通方法处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        sendImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.
                        Media.DATA));
            }
            cursor.close();
        }
        Log.i(path, "相册选择");
        return path;
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        Log.i(imagePath, "相册选择5");
        sendImage(imagePath);
    }

    private void sendImage(String imagePath) {
        Log.d("hhh","send image message "+imagePath);
        BmobIMImageMessage image = new BmobIMImageMessage(imagePath);
        mConversationManager.sendMessage(image, listener);
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 发送文本信息
     */
    private void sendMessage() {
        String text = edit_msg.getText().toString();
        if (TextUtils.isEmpty(text.trim())) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobIMTextMessage msg = new BmobIMTextMessage();
        Log.d("hhh","home activity 里面user"+HomeActivity.user.getObjectId()+";"+HomeActivity.user.getName());
        BmobIMUserInfo info=new BmobIMUserInfo(HomeActivity.user.getObjectId(),
                HomeActivity.user.getName(),HomeActivity.user.getUserIcon());
       // msg.setBmobIMUserInfo(info);
        msg.setContent(text);
        //可随意设置额外信息
        Map<String, Object> map = new HashMap<>();
        map.put("level", "1");
        msg.setExtraMap(map);
        msg.setExtra("OK");
        mConversationManager.sendMessage(msg, listener);
    }


    private MessageSendListener listener = new MessageSendListener() {
        @Override
        public void onProgress(int i) {
            super.onProgress(i);
            Log.d("hhh", "进度为" + i);
        }
        @Override
        public void onStart(BmobIMMessage bmobIMMessage) {
            super.onStart(bmobIMMessage);
            adapter.addMessage(bmobIMMessage);
            edit_msg.setText("");
            scrollToBottom();
        }
        @Override
        public void done(BmobIMMessage bmobIMMessage, BmobException e) {
            adapter.notifyDataSetChanged();
            edit_msg.setText("");
            Log.d("hhh", "信息发送成功");
            scrollToBottom();
            if (e != null) {
                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);
    }
}
