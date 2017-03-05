package com.nghianh.giaitriviet.model;

import java.util.ArrayList;

/**
 * Created by NghiaNH on 3/3/2017.
 */

public class GroupTVChannel {

    private String groupName;
    private String imgUrl;
    private ArrayList<TVChannel> tvList;

    public GroupTVChannel(String groupName, String imgUrl, ArrayList<TVChannel> tvList) {
        this.groupName = groupName;
        this.imgUrl = imgUrl;
        this.tvList = tvList;
    }

    public GroupTVChannel() {

    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public ArrayList<TVChannel> getTvList() {
        return tvList;
    }

    public void setTvList(ArrayList<TVChannel> tvList) {
        this.tvList = tvList;
    }
}
