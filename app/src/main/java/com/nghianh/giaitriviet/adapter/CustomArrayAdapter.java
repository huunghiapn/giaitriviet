package com.nghianh.giaitriviet.adapter;

import android.app.Activity;
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

/**
 * Created by NghiaNH on 3/2/2017.
 */

public class CustomArrayAdapter extends BaseAdapter {

    private Context context; //context
    private ArrayList<TVChannel> items; //data source of the list adapter

    public CustomArrayAdapter(Context context, int resource, ArrayList<TVChannel> items) {
        this.context = context;
        this.items = items;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return items.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.list_view_item, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }

        TVChannel item = (TVChannel) getItem(position);

        holder.label.setText(item.getChannelName());
        Picasso
                .with(context)
                .load(item.getImgUrl())
                .resize(200, 200)
                .error(R.mipmap.ic_launcher)
                .into(holder.image);

        return convertView;
    }

    private static class ViewHolder {
        private TextView label;
        private ImageView image;

        public ViewHolder(View v) {
            image = (ImageView) v.findViewById(R.id.imgLogo);
            label = (TextView) v.findViewById(R.id.label);
        }
    }
}
