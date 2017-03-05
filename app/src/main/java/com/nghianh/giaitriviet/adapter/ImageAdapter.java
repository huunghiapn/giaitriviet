package com.nghianh.giaitriviet.adapter;

/**
 * Created by NghiaNH on 3/3/2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nghianh.giaitriviet.R;
import com.nghianh.giaitriviet.model.TVChannel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater = null;
    private ArrayList<TVChannel> listViewItems;

    public ImageAdapter(Context c, String title, ArrayList<TVChannel> listViewItems) {
        mContext = c;
        this.listViewItems = listViewItems;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return listViewItems.size();
    }

    @Override
    public String getItem(int position) {
        return listViewItems.get(position).getImgUrl();
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.grid_view_items, null);
        holder.tv = (TextView) rowView.findViewById(R.id.txtName);
        holder.img = (ImageView) rowView.findViewById(R.id.imageLogo);
        String url = getItem(position);
        holder.tv.setText(listViewItems.get(position).getChannelName());
        Picasso.with(mContext)
                .load(url)
                .placeholder(R.drawable.play_icon)
                .resize(480, 480)
                .into(holder.img);
        /*ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(480, 480));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        String url = getItem(position);
        Picasso.with(mContext)
                .load(url)
                .placeholder(R.drawable.play_icon)
                .resize(480, 480)
                .into(imageView);*/
        return rowView;
    }

    public class Holder {
        TextView tv;
        ImageView img;
    }
}