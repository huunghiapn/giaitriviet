package com.nghianh.giaitriviet.providers.wordpress.api.providers;

import com.nghianh.giaitriviet.providers.wordpress.PostItem;
import com.nghianh.giaitriviet.providers.wordpress.api.WordpressGetTaskInfo;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This is an interface for Wordpress API Providers.
 */
public interface WordpressProvider {

    String getRecentPosts(WordpressGetTaskInfo info);

    String getTagPosts(WordpressGetTaskInfo info, String tag);

    String getCategoryPosts(WordpressGetTaskInfo info, String category);

    String getSearchPosts(WordpressGetTaskInfo info, String query);

    ArrayList<PostItem> parsePosts(WordpressGetTaskInfo info, JSONObject response);

}
