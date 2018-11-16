package com.example.lostgoodliness.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.activity.GoodsDetailsInfoActivity;
import com.example.lostgoodliness.javabean.LostTable;
import com.example.lostgoodliness.javabean.Users;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 佳佳 on 10/11/2018.
 */

public class LostSearchResultAdapter extends RecyclerView.Adapter<LostSearchResultAdapter.ViewHolder> {

    private List<LostTable> mLostSearchResult;
    private Context context;
    private boolean isScrolling = false;   //监视当前是否处在滑动状态
    private ImageLoader imageLoader;
    private DisplayImageOptions options;


    static class ViewHolder extends RecyclerView.ViewHolder {
        View infoView;
        CircleImageView userIcon;
        TextView textView;
        TextView phoneTV;
        TextView date;


        public ViewHolder(View view) {
            super(view);
            infoView = view;
            userIcon = (CircleImageView) view.findViewById(R.id.user_icon);
            textView = (TextView) view.findViewById(R.id.resultInfo);
            phoneTV = (TextView) view.findViewById(R.id.phone);
            date = (TextView) view.findViewById(R.id.date);
        }
    }

    public void setScrolling(boolean scrolling) {
        isScrolling = scrolling;
    }

    /**
     * 初始化参数
     * @param searchResults
     * @param context
     * @param imageLoader
     * @param options
     */
    public LostSearchResultAdapter(List<LostTable> searchResults,  Context context, ImageLoader imageLoader, DisplayImageOptions options) {
        this.mLostSearchResult = searchResults;
        this.context = context;
        this.imageLoader = imageLoader;
        this.options = options;
    }

    @Override
    public LostSearchResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_item_layout
                , parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                LostTable lostTable = mLostSearchResult.get(position);
                Intent intent = new Intent(context, GoodsDetailsInfoActivity.class);
                intent.putExtra("type", "lost");
                intent.putExtra("lostInfo", lostTable);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final LostSearchResultAdapter.ViewHolder holder, int position) {
        LostTable lostTable = mLostSearchResult.get(position);
        String display = lostTable.getUserName();
        if (lostTable != null) {
            display += "在" + lostTable.getCity() + "丢失了一个" + lostTable.getLostType();
        } else {
            display += "丢失了一个" + lostTable.getLostType();
        }
        holder.textView.setText(display);
        holder.phoneTV.setText(lostTable.getPhone());
        holder.date.setText(lostTable.getLostTime());
        holder.userIcon.setTag(position);
        //设置占位符
        //holder.userIcon.setImageDrawable(context.getDrawable(R.mipmap.home_background));
        //设置tag
        String userIcon=lostTable.getLinkUsers().getUserIcon();
        holder.userIcon.setTag(userIcon);
       /* if (!isScrolling && userIcon.equals(holder.userIcon.getTag())) {

        }*/
        imageLoader.displayImage(userIcon, holder.userIcon, options);
    }


    @Override
    public int getItemCount() {
        return mLostSearchResult.size();
    }
}
