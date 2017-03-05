package com.nghianh.giaitriviet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.nghianh.giaitriviet.R;
import com.nghianh.giaitriviet.activity.TVActivity;
import com.nghianh.giaitriviet.adapter.ImageAdapter;
import com.nghianh.giaitriviet.model.TVChannel;
import com.nghianh.giaitriviet.util.Helper;

import java.util.ArrayList;

/**
 * Created by NghiaNH on 3/2/2017.
 */

public class ChannelFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private ListView listView;
    private ArrayList<TVChannel> listViewItems;

    // newInstance constructor for creating fragment with arguments
    public static ChannelFragment newInstance(int page, String title) {
        ChannelFragment fragmentFirst = new ChannelFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        listViewItems = Helper.getChannelList(getContext(), title);

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel, container, false);

        final GridView gridview = (GridView) view.findViewById(R.id.gridview);

        gridview.setAdapter(new ImageAdapter(getContext(), title, listViewItems));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(getContext(), TVActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra(TVActivity.ITEM_LIST, listViewItems);
                intent.putExtra(TVActivity.ITEM_POS, position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }

    public void showToastMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG)
                .show();
    }
}