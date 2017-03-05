package com.nghianh.giaitriviet.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nghianh.giaitriviet.fragment.ChannelFragment;
import com.nghianh.giaitriviet.model.GroupTVChannel;

import java.util.ArrayList;

/**
 * Created by NghiaNH on 3/2/2017.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {
    ArrayList<GroupTVChannel> tvList;

    public MyPagerAdapter(FragmentManager fragmentManager, ArrayList<GroupTVChannel> tvList) {
        super(fragmentManager);
        this.tvList = tvList;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return tvList.size();
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        return ChannelFragment.newInstance(position, tvList.get(position).getGroupName());
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return tvList.get(position).getGroupName();
    }

}
