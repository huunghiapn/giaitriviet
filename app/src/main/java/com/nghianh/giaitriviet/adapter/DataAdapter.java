package com.nghianh.giaitriviet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nghianh.giaitriviet.R;
import com.nghianh.giaitriviet.model.RecycleViewItem;
import com.squareup.picasso.Picasso;

import org.jsoup.helper.StringUtil;

import java.util.ArrayList;

/**
 * Created by NghiaNH on 3/2/2017.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<RecycleViewItem> android_versions;
    private Context context;

    public DataAdapter(Context context, ArrayList<RecycleViewItem> android_versions) {
        this.context = context;
        this.android_versions = android_versions;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        viewHolder.tv_android.setText(android_versions.get(i).getAndroid_version_name());
        if (StringUtil.isBlank(android_versions.get(i).getAndroid_image_url())) {
            Picasso.with(context).load(android_versions.get(i).getAndroid_image_url()).resize(120, 60).error(R.mipmap.ic_launcher).into(viewHolder.img_android);
        } else {
            Picasso.with(context).load(R.drawable.play_icon).resize(120, 60).error(R.mipmap.ic_launcher).into(viewHolder.img_android);
        }
    }

    @Override
    public int getItemCount() {
        return android_versions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_android;
        ImageView img_android;

        public ViewHolder(View view) {
            super(view);

            tv_android = (TextView) view.findViewById(R.id.tv_android);
            img_android = (ImageView) view.findViewById(R.id.img_android);
        }
    }
}