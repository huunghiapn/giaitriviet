package com.nghianh.giaitriviet.providers.yt.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.nghianh.giaitriviet.R;
import com.nghianh.giaitriviet.activity.MainActivity;
import com.nghianh.giaitriviet.providers.yt.VideosAdapter;
import com.nghianh.giaitriviet.providers.yt.api.RetrieveVideos;
import com.nghianh.giaitriviet.providers.yt.api.object.ReturnItem;
import com.nghianh.giaitriviet.providers.yt.api.object.Video;
import com.nghianh.giaitriviet.util.Helper;
import com.nghianh.giaitriviet.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This activity is used to display a list of vidoes
 */
public class VideosFragment extends Fragment {
    private static int TYPE_SEARCH = 1;
    private static int TYPE_PLAYLIST = 2;
    public RelativeLayout pDialog;
    //Layout references
    private ListView listView;
    private View footerView;
    private LinearLayout ll;
    private Activity mAct;
    //Stores information
    private ArrayList<Video> videoList;
    private VideosAdapter videoAdapter;
    private RetrieveVideos videoApiClient;
    //Keeping track of location & status
    private String upcomingPageToken;
    private boolean isLoading = true;
    private boolean isFirstLoad = true;
    private int currentType;
    private String searchQuery;
    private List<Map<String, String>> ListYT;
    private InterstitialAd mInterstitialAd;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ll = (LinearLayout) inflater.inflate(R.layout.fragment_list_nopadding, container, false);
        setHasOptionsMenu(true);

        //checking if the user has just opened the app
        footerView = inflater.inflate(R.layout.listview_footer, null);
        pDialog = (RelativeLayout) ll.findViewById(R.id.progressBarHolder);
        listView = (ListView) ll.findViewById(R.id.list);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                Video video = (Video) o;
                Intent intent = new Intent(mAct, VideoDetailActivity.class);
                intent.putExtra(VideoDetailActivity.EXTRA_VIDEO, video);
                startActivity(intent);
            }
        });

        listView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (listView == null)
                    return;

                if (listView.getCount() == 0)
                    return;

                int l = visibleItemCount + firstVisibleItem;
                if (l >= totalItemCount && !isLoading) {
                    // It is time to add new data. We call the listener
                    if (null != upcomingPageToken) {
                        loadVideos(upcomingPageToken);
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });

        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId(getString(R.string.interstitialAd_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }
        });
        requestNewInterstitial();

        return ll;
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("D00090843C9FDF13E3391548194698B9")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAct = getActivity();

        ListYT = Helper.getYTSetting(getContext());

        String apiKey = getResources().getString(R.string.google_server_key);
        videoApiClient = new RetrieveVideos(mAct, apiKey);

        videoList = new ArrayList<>();
        videoAdapter = new VideosAdapter(mAct, videoList);

        //Set the default type
        currentType = TYPE_PLAYLIST;
        //Load the youtube videos
        //loadVideos(null);
    }

    //@param nextpagetoken leave at null to get first page
    //currentType must be set
    private void loadVideos(String nextPageToken) {
        String channeldid = null;
        if (getPassedData().length > 1) {
            channeldid = getPassedData()[1];
        }

        String param = null;
        if (currentType == TYPE_PLAYLIST) {
            param = getPassedData()[0];
        } else if (currentType == TYPE_SEARCH) {
            param = searchQuery;
        }

        loadVideosInList(nextPageToken, param, channeldid);
    }

    //@param nextPageToken the token of the page to load, null if the first page
    //@param param the username or query
    //@param retrievaltype the type of retrieval to do, either TYPE_SEARCH or TYPE_PLAYLIST
    private void loadVideosInList(final String nextPageToken, final String param, final String channelID) {

        listView.addFooterView(footerView);
        if (listView.getAdapter() == null) {
            listView.setAdapter(videoAdapter);
        }
        isLoading = true;

        if (nextPageToken == null) {
            videoList.clear();
            videoAdapter.notifyDataSetChanged();
            upcomingPageToken = null;
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                ReturnItem result = null;
                if (currentType == TYPE_SEARCH) {
                    result = videoApiClient.getSearchVideos(param, channelID, nextPageToken);
                    Log.v("INFO", "Performing search");
                } else if (currentType == TYPE_PLAYLIST) {
                    result = videoApiClient.getUserVideos(param, nextPageToken);
                }

                final ArrayList<Video> videos = result.getList();
                upcomingPageToken = result.getPageToken();

                mAct.runOnUiThread(new Runnable() {
                    public void run() {

                        listView.removeFooterView(footerView);

                        isLoading = false;

                        //Hide the loading layout that is shown during the initial load
                        if (pDialog.getVisibility() == View.VISIBLE) {
                            pDialog.setVisibility(View.INVISIBLE);
                            Helper.revealView(listView, ll);
                        }

                        if (videos != null) {
                            if (videos.size() > 0)
                                videoList.addAll(videos);
                        } else {
                            Helper.noConnection(mAct);
                        }

                        videoAdapter.notifyDataSetChanged();
                    }
                });

            }
        });

    }

    private String[] getPassedData() {
        String[] parts = getArguments().getStringArray(MainActivity.FRAGMENT_DATA);
        return parts;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh_menu, menu);
        final String yt_channel = this.getArguments().getStringArray(MainActivity.FRAGMENT_DATA)[2];
        final String[] lblYTName = Arrays.copyOf(ListYT.get(Integer.parseInt(yt_channel)).keySet().toArray(), ListYT.get(Integer.parseInt(yt_channel)).size(), String[].class);
        final String[] lblYTUrl = Arrays.copyOf(ListYT.get(Integer.parseInt(yt_channel)).values().toArray(), ListYT.get(Integer.parseInt(yt_channel)).size(), String[].class);

        MenuItem item = menu.findItem(R.id.spinner);
        final Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<String> adapter = null;

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lblYTName);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
                toolbar.setTitle(spinner.getSelectedItem().toString());
                if (isFirstLoad) {
                    isFirstLoad = false;
                } else {
                    loadVideosInList(null, lblYTUrl[position], getPassedData()[1]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //set & get the search button in the actionbar
        final SearchView searchView = new SearchView(mAct);

        searchView.setQueryHint(getResources().getString(R.string.video_search_hint));
        searchView.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                currentType = TYPE_SEARCH;
                loadVideos(null);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        String[] parts = getPassedData();

        searchView.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {

            @Override
            public void onViewDetachedFromWindow(View arg0) {
                if (!isLoading) {
                    currentType = TYPE_PLAYLIST;
                    searchQuery = null;
                    loadVideos(null);
                }
            }

            @Override
            public void onViewAttachedToWindow(View arg0) {
                // search was opened
            }
        });


        if (parts.length == 2) {
            menu.add("search")
                    .setIcon(R.drawable.ic_action_search)
                    .setActionView(searchView)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }
        //TODO make menu an xml item
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.refresh:
                if (!isLoading) {
                    loadVideos(null);
                } else {
                    Toast.makeText(mAct, getString(R.string.already_loading), Toast.LENGTH_LONG).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}