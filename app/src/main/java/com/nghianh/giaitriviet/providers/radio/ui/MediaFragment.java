package com.nghianh.giaitriviet.providers.radio.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.nghianh.giaitriviet.R;
import com.nghianh.giaitriviet.activity.MainActivity;
import com.nghianh.giaitriviet.fragment.PermissionsFragment;
import com.nghianh.giaitriviet.providers.radio.NotificationUpdater;
import com.nghianh.giaitriviet.providers.radio.Radio_Link;
import com.nghianh.giaitriviet.providers.radio.parser.AlbumArtGetter;
import com.nghianh.giaitriviet.providers.radio.parser.UrlParser;
import com.nghianh.giaitriviet.providers.radio.visualizer.DrawingPanel;
import com.nghianh.giaitriviet.util.Helper;
import com.nghianh.giaitriviet.util.Log;

import co.mobiwise.library.radio.RadioListener;
import co.mobiwise.library.radio.RadioManager;

/**
 * This fragment is used to listen to a radio station
 */
public class MediaFragment extends Fragment implements OnClickListener, RadioListener, PermissionsFragment {

    //Auto error solving
    private static int RETRY_INTERVAL = 7000;
    private static int RETRY_MAX = 2;
    private static int audioSessionID = 0;
    private RadioManager mRadioManager;
    private boolean runningOnOldConnection;
    private String[] arguments;
    private String urlToPlay;
    private Activity mAct;
    //Layouts
    private DrawingPanel dPanel;
    private ImageView imageView;
    private LinearLayout ll;
    private ProgressBar loadingIndicator;
    private Button buttonPlay;
    private Button buttonStopPlay;
    private int errorcount = 0;
    //If we should use a visualizer or album art
    private boolean VISUALIZER_ENABLED = true;
    private InterstitialAd mInterstitialAd;
    private Handler myHandler;
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
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
        }
    };


    /**
     * Called when the activity is first created.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ll = (LinearLayout) inflater.inflate(R.layout.fragment_radio, container, false);
        setHasOptionsMenu(true);
        initializeUIElements();
        myHandler = new Handler();
        myHandler.postDelayed(myRunnable, 300000);

        //Get the arguments and 'parse' them
        arguments = MediaFragment.this.getArguments().getStringArray(MainActivity.FRAGMENT_DATA);
        if (arguments.length > 1)
            VISUALIZER_ENABLED = arguments[1].equals("visualizer");

        //Initialize visualizer or imageview for album art
        if (VISUALIZER_ENABLED) {
            int vType = 15;
            int cMode = 0;
            int cMode2 = 0;
            boolean frequency = true;
            dPanel = new DrawingPanel(getActivity(), vType, cMode, cMode2, frequency, audioSessionID);

            ((RelativeLayout) ll.findViewById(R.id.visualizerView)).addView(dPanel, 0);
        } else {
            imageView = new ImageView(ll.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setBackground(getResources().getDrawable(R.drawable.radio_background));

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);

            imageView.setLayoutParams(params);

            ((RelativeLayout) ll.findViewById(R.id.visualizerView)).addView(imageView, 0);
        }

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

        Helper.isOnline(mAct, true);

        //Get the radioManager
        mRadioManager = RadioManager.with(mAct);

        mRadioManager.registerListener(NotificationUpdater.getStaticNotificationUpdater(mAct.getBaseContext()));

        //If we are already playing, wait until stop is clicked before re-connecting from this thread
        if (!mRadioManager.isConnected()) {
            mRadioManager.connect();
            runningOnOldConnection = false;
        } else {
            runningOnOldConnection = true;
        }

        //Parse the url on the background
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                urlToPlay = (UrlParser.getUrl(arguments[0]));

                if (isPlaying()) {
                    if (!RadioManager.getService().getRadioUrl().equals(urlToPlay)) {
                        mAct.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(mAct, getResources().getString(R.string.radio_playing_other), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }

        });

    }

    private void initializeUIElements() {
        loadingIndicator = (ProgressBar) ll.findViewById(R.id.loadingIndicator);
        loadingIndicator.setMax(100);
        loadingIndicator.setVisibility(View.INVISIBLE);

        buttonPlay = (Button) ll.findViewById(R.id.btn_play);
        buttonPlay.setOnClickListener(this);

        buttonStopPlay = (Button) ll.findViewById(R.id.btn_pause);
        buttonStopPlay.setOnClickListener(this);

        updateButtons();
    }

    public void updateButtons() {
        if (isPlaying() || loadingIndicator.getVisibility() == View.VISIBLE) {
            buttonPlay.setEnabled(false);
            buttonStopPlay.setEnabled(true);
        } else {
            buttonPlay.setEnabled(true);
            buttonStopPlay.setEnabled(false);

            updateMediaInfoFromBackground(null);
        }
    }

    public void onClick(View v) {
        if (v == buttonPlay) {
            if (urlToPlay != null) {
                startPlaying();

                //Check the sound level
                AudioManager am = (AudioManager) mAct.getSystemService(Context.AUDIO_SERVICE);
                int volume_level = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (volume_level < 2) {
                    Toast.makeText(mAct, getResources().getString(R.string.volume_low), Toast.LENGTH_SHORT).show();
                }
            } else {
                //The loading of urlToPlay should happen almost instantly, so this code should never be reached
            }
        } else if (v == buttonStopPlay) {
            stopPlaying();
        }
    }

    private void startPlaying() {
        //Show loading view
        loadingIndicator.setVisibility(View.VISIBLE);

        //Start the radio playing and set default notification
        mRadioManager.startRadio(urlToPlay);
        mRadioManager.updateNotification(mAct.getResources().getString(R.string.notification_playing), "",
                R.drawable.ic_radio_playing,
                BitmapFactory.decodeResource(
                        mAct.getResources(),
                        co.mobiwise.library.R.drawable.default_art));

        //Update the UI
        updateButtons();
    }

    private void stopPlaying() {

        //Stop the radio playing
        mRadioManager.stopRadio();

        //Hide loading layout if shown
        loadingIndicator.setVisibility(View.INVISIBLE);

        //Update the UI
        updateButtons();

        //Do a 'reset' if we're using a player from a different url
        if (runningOnOldConnection) {
            resetRadioManager();
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //final String tv_chanels = this.getArguments().getStringArray(MainActivity.FRAGMENT_DATA)[1];
        inflater.inflate(R.menu.tv_menu, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        final Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<CharSequence> adapter = null;

        adapter = ArrayAdapter.createFromResource(getContext(), R.array.RADIO_CHANNEL, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
                toolbar.setTitle(spinner.getSelectedItem().toString());
                if (isPlaying()) {
                    stopPlaying();
                    runningOnOldConnection = true;
                }

                urlToPlay = Radio_Link.VN_VOV[position];
                if (runningOnOldConnection) {
                    startPlaying();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //@param info - the text to be updated. Giving a null string will hide the info.
    public void updateMediaInfoFromBackground(String info) {
        TextView nowPlayingTitle = (TextView) ll.findViewById(R.id.now_playing_title);
        TextView nowPlaying = (TextView) ll.findViewById(R.id.now_playing);

        if (info != null)
            nowPlaying.setText(info);

        if (info != null && nowPlayingTitle.getVisibility() == View.GONE) {
            nowPlayingTitle.setVisibility(View.VISIBLE);
            nowPlaying.setVisibility(View.VISIBLE);
        } else if (info == null) {
            nowPlayingTitle.setVisibility(View.GONE);
            nowPlaying.setVisibility(View.GONE);
        }
    }

    @Override
    public String[] requiredPermissions() {
        return new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE};
    }

    @Override
    public void onRadioLoading() {

    }

    @Override
    public void onRadioConnected() {
    }

    @Override
    public void onRadioStarted() {
        mAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //Hide the loading indicator
                loadingIndicator.setVisibility(View.INVISIBLE);

                //Update buttons
                updateButtons();
            }
        });
    }

    @Override
    public void onRadioStopped() {
        mAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //Hide the loading indicator
                loadingIndicator.setVisibility(View.INVISIBLE);

                //Update buttons
                updateButtons();

                //Only if the fragment is already on the foreground hide the notification
                if (MediaFragment.this.isVisible())
                    RadioManager.getService().cancelNotification();
            }
        });
    }

    @Override
    public void onMetaDataReceived(String key, final String value) {
        if (key != null && (key.equals("StreamTitle") || key.equals("title")) && !value.equals("")) {

            //Update the mediainfo shown above the controls
            mAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateMediaInfoFromBackground(value);
                }
            });

            //Show album art based on the metadata
            updateAlbumArt(value);

        }
    }

    @Override
    public void onAudioSessionId(int i) {
        audioSessionID = i;
        if (VISUALIZER_ENABLED) {
            dPanel.setAudioSessionID(audioSessionID);
        }
    }

    @Override
    public void onError() {
        Log.v("INFO", "onerror");
        mAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (errorcount < RETRY_MAX) {
                    loadingIndicator.setVisibility(View.VISIBLE);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            errorcount += 1;
                            startPlaying();
                        }
                    }, RETRY_INTERVAL);
                } else {
                    //Inform the user
                    Toast.makeText(mAct, mAct.getResources().getString(R.string.error_retry), Toast.LENGTH_SHORT).show();
                    Log.v("INFO", "Received various errors, tried to create a new RadioManager");

                    //Do the 'reset'
                    resetRadioManager();

                    //Update the UI
                    loadingIndicator.setVisibility(View.INVISIBLE);
                    updateButtons();
                }
            }
        });
    }

    @Override
    public void onResume() {
        updateButtons();
        super.onResume();

        //Register for updates
        mRadioManager.registerListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        //Unregister from updates
        mRadioManager.unregisterListener(this);
    }

    private boolean isPlaying() {
        return (null != mRadioManager && null != RadioManager.getService() && RadioManager.getService().isPlaying());
    }

    private void resetRadioManager() {
        try {
            mRadioManager.disconnect();
        } catch (Exception e) {
            //Do nothing, apparently we where not connected in the first place.
        }
        RadioManager.flush();
        mRadioManager = RadioManager.with(mAct);
        mRadioManager.connect();
        mRadioManager.registerListener(this);
        mRadioManager.registerListener(NotificationUpdater.getStaticNotificationUpdater(mAct.getBaseContext()));
        runningOnOldConnection = false;
    }

    private void updateAlbumArt(String infoString) {
        if (imageView != null) {
            AlbumArtGetter.getImageForQuery(infoString, new AlbumArtGetter.AlbumCallback() {
                @Override
                public void finished(Bitmap art) {
                    if (art != null) {
                        imageView.setImageBitmap(art);
                    }
                }
            }, mAct);
        }
    }

    @Override
    public void onDestroy() {
        myHandler.removeCallbacks(myRunnable);
        mRadioManager.disconnect();
        super.onDestroy();
    }

}