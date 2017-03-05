package com.nghianh.giaitriviet.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nghianh.giaitriviet.R;
import com.nghianh.giaitriviet.adapter.MyPagerAdapter;
import com.nghianh.giaitriviet.model.GroupTVChannel;
import com.nghianh.giaitriviet.util.Helper;

import java.util.ArrayList;

/**
 * Created by NghiaNH on 3/2/2017.
 */

public class GroupChannelGirdFragment extends Fragment {
    FragmentPagerAdapter adapterViewPager;
    // Store instance variables
    private String title;
    private int page;

    // newInstance constructor for creating fragment with arguments
    public static GroupChannelGirdFragment newInstance(int page, String title) {
        GroupChannelGirdFragment fragmentFirst = new GroupChannelGirdFragment();
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
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_channel, container, false);

        ViewPager vpPager = (ViewPager) view.findViewById(R.id.vpPager);
        ArrayList<GroupTVChannel> tvList = Helper.getGroupChannelList(getContext());
        adapterViewPager = new MyPagerAdapter(getChildFragmentManager(), tvList);
        vpPager.setAdapter(adapterViewPager);
        // Attach the page change listener inside the activity
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                /*Toast.makeText(getContext(),
                        "Selected page position: " + position, Toast.LENGTH_SHORT).show();*/
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

        return view;
    }
}