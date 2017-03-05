package com.nghianh.giaitriviet.model;

import java.io.Serializable;

/**
 * Created by NghiaNH on 3/3/2017.
 */

public class TVChannel implements Serializable {

    private String channelName;
    private String streamUrl;
    private String imgUrl;
    private String groupName;

    public TVChannel(String channelName, String streamUrl, String imgUrl, String groupName) {
        this.channelName = channelName;
        this.streamUrl = streamUrl;
        this.imgUrl = imgUrl;
        this.groupName = groupName;
    }

    public TVChannel() {

    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


}
