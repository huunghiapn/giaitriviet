package com.nghianh.giaitriviet;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.nghianh.giaitriviet.util.Helper;

import fm.jiecao.jcvideoplayer_lib.JCFullScreenActivity;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by NghiaNH on 3/5/2017.
 */

public class JCVideoPlayerAdmob extends JCVideoPlayerStandard {

    private InterstitialAd mInterstitialAd;
    private Handler myHandler = null;
    private long wait = 180000;
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            mInterstitialAd = new InterstitialAd(getContext());
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitialAd_id));
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    wait = wait + wait;
                    myHandler.postDelayed(myRunnable, wait);
                    super.onAdClosed();
                    //jcVideoPlayerStandard.startButton.performClick();
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

    public JCVideoPlayerAdmob(Context context) {
        super(context);
    }

    public JCVideoPlayerAdmob(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("D00090843C9FDF13E3391548194698B9")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public boolean setUp(String url, Object... objects) {
        if (mIfCurrentIsFullscreen) {
            final JCFullScreenActivity activity = (JCFullScreenActivity) getContext();
            final AdView mAdView = new AdView(getContext());
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(40, 40);
                    lparams.topMargin = mAdView.getBottom();
                    lparams.gravity = Gravity.CENTER_HORIZONTAL;
                    final ImageButton btn = new ImageButton(activity);
                    btn.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                    btn.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            btn.setVisibility(View.GONE);
                            mAdView.destroy();
                        }
                    });
                    btn.setLayoutParams(lparams);
                    activity.addContentView(btn, lparams);
                }
            });
            RelativeLayout.LayoutParams params = new RelativeLayout
                    .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            mAdView.setLayoutParams(params);
            mAdView.setId(R.id.adView);
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(getResources().getString(R.string.ad_id));
            activity.addContentView(mAdView, params);
            Helper.admobLoader(getContext(), getResources(), mAdView);
        } else {
            myHandler = new Handler();
            myHandler.postDelayed(myRunnable, 180000);
        }

        return super.setUp(url, objects);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (myHandler != null) {
            myHandler.removeCallbacks(myRunnable);
        }
        JCVideoPlayer.releaseAllVideos();
        super.onDetachedFromWindow();
    }
}
