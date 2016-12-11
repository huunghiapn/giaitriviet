package com.nghianh.giaitriviet;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.nghianh.giaitriviet.drawer.NavDrawerCallback;
import com.nghianh.giaitriviet.drawer.NavDrawerFragment;
import com.nghianh.giaitriviet.drawer.NavItem;
import com.nghianh.giaitriviet.providers.rss.ServiceStarter;
import com.nghianh.giaitriviet.util.Helper;
import com.nghianh.giaitriviet.util.Log;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

public class MainActivity extends AppCompatActivity implements NavDrawerCallback {

    //Data to pass to a fragment
    public static String FRAGMENT_DATA = "transaction_data";
    public static String FRAGMENT_CLASS = "transation_target";
    public static boolean TABLET_LAYOUT = true;
    //Permissions Queu
    NavItem queueItem;
    SharedPreferences prefs;
    private Toolbar mToolbar;
    private NavDrawerFragment mNavigationDrawerFragment;
    public static final int progress_bar_type = 0;

    public static final String PREF_KEY_FIRST_START = "com.nghianh.giaitriviet.PREF_KEY_FIRST_START";
    public static final int REQUEST_CODE_INTRO = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8092675209428225~8500010598");

        if (useTabletMenu()) {
            setContentView(R.layout.activity_main_tablet);
            Helper.setStatusBarColor(MainActivity.this,
                    ContextCompat.getColor(this, R.color.myPrimaryDarkColor));
        } else if (Config.USE_NEW_DRAWER) {
            setContentView(R.layout.activity_main_alternate);
        } else {
            setContentView(R.layout.activity_main);
            Helper.setStatusBarColor(MainActivity.this,
                    ContextCompat.getColor(this, R.color.myPrimaryDarkColor));
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        if (!useTabletMenu())
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        else {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }

        mNavigationDrawerFragment = (NavDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_drawer);

        if (Config.USE_NEW_DRAWER && !useTabletMenu()) {
            mNavigationDrawerFragment.setup(R.id.scrimInsetsFrameLayout,
                    (DrawerLayout) findViewById(R.id.drawer), mToolbar);
            mNavigationDrawerFragment
                    .getDrawerLayout()
                    .setStatusBarBackgroundColor(
                            ContextCompat.getColor(this, R.color.myPrimaryDarkColor));

            findViewById(R.id.scrimInsetsFrameLayout).getLayoutParams().width = getDrawerWidth();
        } else {
            mNavigationDrawerFragment.setup(R.id.fragment_drawer,
                    (DrawerLayout) findViewById(R.id.drawer), mToolbar);

            DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) mNavigationDrawerFragment.getView().getLayoutParams();
            params.width = getDrawerWidth();
            mNavigationDrawerFragment.getView().setLayoutParams(params);
        }

        if (useTabletMenu()) {
            mNavigationDrawerFragment
                    .getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            mNavigationDrawerFragment
                    .getDrawerLayout().setScrimColor(Color.TRANSPARENT);
        } else {
            mNavigationDrawerFragment
                    .getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }

        Helper.admobLoader(this, getResources(), findViewById(R.id.adView));

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        Helper.updateAndroidSecurityProvider(this);

        // setting push enabled
        String push = getString(R.string.rss_push_url);
        if (null != push && !push.equals("")) {
            // Create object of SharedPreferences.
            boolean firstStart = prefs.getBoolean("firstStart", true);

            if (firstStart) {
                Intent intent = new Intent(this, SplashIntroActivity.class);
                startActivityForResult(intent, REQUEST_CODE_INTRO);

                final ServiceStarter alarm = new ServiceStarter();

                SharedPreferences.Editor editor = prefs.edit();

                alarm.setAlarm(this);
                // now, just to be sure, where going to set a value to check if
                // notifications is really enabled
                editor.putBoolean("firstStart", false);
                // commits your edits
                editor.commit();
            }

        }

        // Check if we should open a fragment based on the arguments we have
        boolean loadedFragment = false;
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(FRAGMENT_CLASS)) {
            try {
                Class<? extends Fragment> fragmentClass = (Class<? extends Fragment>) getIntent().getExtras().getSerializable(FRAGMENT_CLASS);
                if (fragmentClass != null) {
                    String[] extra = getIntent().getExtras().getStringArray(FRAGMENT_DATA);

                    Fragment fragment = fragmentClass.newInstance();
                    showFragment(fragment, extra, getTitle().toString());
                    loadedFragment = true;
                }
            } catch (Exception e) {
                //If we come across any errors, just continue and open the default fragment
                Log.printStackTrace(e);
            }
        }

        //If we haven't already loaded an item (or came from rotation and there was already an item)
        //Load the first item
        if (savedInstanceState == null && !loadedFragment) {
            mNavigationDrawerFragment.loadInitialItem();
        }

        // Checking if the user would prefer to show the menu on start
        boolean checkBox = prefs.getBoolean("menuOpenOnStart", false);
        if (checkBox && !useTabletMenu()) {
            mNavigationDrawerFragment.openDrawer();
        }

        AppRate.with(this)
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(3) // default 10
                .setRemindInterval(2) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.rss_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                boolean foundfalse = false;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        foundfalse = true;
                    }
                }
                if (!foundfalse) {
                    openNavigationItem(queueItem);
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.permissions_required), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //Perform the opening of a (selected) navItem
    private void openNavigationItem(NavItem item) {
        try {
            Fragment fragment = item.getFragment().newInstance();

            if (fragment != null) {

                //Verify if we can safely open the fragment by checking for permissions
                if (checkPermissionsHandleIfNeeded(item, fragment) && checkPurchaseHandleIfNeeded(item)) {
                    String[] extra = item.getData();

                    if (fragment instanceof CustomIntent) {
                        CustomIntent.performIntent(MainActivity.this, extra);
                    } else {
                        showFragment(fragment, extra, item.getText(this));
                    }
                } else {
                    //We do nothing, the check method will handle this for us.
                }

            } else {
                Log.v("INFO", "Error creating fragment");
            }
        } catch (InstantiationException e) {
            Log.printStackTrace(e);
        } catch (IllegalAccessException e) {
            Log.printStackTrace(e);
        }

    }

    //Show a fragment in the MainActivity's fragment viewer
    public void showFragment(Fragment fragment, String[] extra, String title) {
        Bundle bundle = new Bundle();

        bundle.putStringArray(FRAGMENT_DATA, extra);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment).commitAllowingStateLoss();

        if (!useTabletMenu())
            setTitle(title);
    }

    @SuppressLint("NewApi")
    @Override
    public void onNavigationDrawerItemSelected(int position, NavItem item) {
        openNavigationItem(item);
    }

    /**
     * If the item can be opened because it either has been purchased or does not require a purchase to show.
     *
     * @param item Item to check
     * @return true if the item is safe to be opened. False if the item is not safe to open, simply don't open as we will handle.
     */
    private boolean checkPurchaseHandleIfNeeded(NavItem item) {
        String license = getResources().getString(R.string.google_play_license);
        // if item does not require purchase, or app has purchased, or license is null/empty (app has no in app purchases)
        if (item.requiresPurchase() == true
                && !SettingsFragment.getIsPurchased(this)
                && null != license && !license.equals("")) {
            Fragment fragment = new SettingsFragment();
            String[] extra = new String[]{SettingsFragment.SHOW_DIALOG};
            showFragment(fragment, extra, item.getText(this));

            return false;
        }

        return true;
    }

    /**
     * Checks if the item can be opened because it has sufficient permissions.
     *
     * @param item     The item to check
     * @param fragment The fragment instance associated to this item.
     * @return true if the item is safe to open
     */
    private boolean checkPermissionsHandleIfNeeded(NavItem item, Fragment fragment) {
        if (fragment instanceof PermissionsFragment && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            String[] permissions = ((PermissionsFragment) fragment).requiredPermissions();

            boolean allGranted = true;
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                    allGranted = false;
            }

            if (!allGranted) {
                //TODO An explaination before asking
                requestPermissions(permissions, 1);
                queueItem = item;
                return false;
            }

            return true;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        JCVideoPlayer.releaseAllVideos();
        Fragment activeFragment = getSupportFragmentManager().findFragmentById(
                R.id.container);

        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        } else if (activeFragment instanceof BackPressFragment) {
            boolean handled = ((BackPressFragment) activeFragment).handleBackPress();
            if (!handled) {
                super.onBackPressed();
            }
        } else {
            //super.onBackPressed();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.dialog_exit))
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null)
            for (Fragment frag : fragments)
                if (frag != null)
                    frag.onActivityResult(requestCode, resultCode, data);
    }

    //Get the width of the drawer
    private int getDrawerWidth() {
        // Navigation Drawer layout width
        int width = getResources().getDisplayMetrics().widthPixels;

        TypedValue tv = new TypedValue();
        int actionBarHeight;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        } else {
            actionBarHeight = 0;
        }

        int possibleMinDrawerWidth = width - actionBarHeight;

        int maxDrawerWidth = getResources().getDimensionPixelSize(R.dimen.drawer_width);

        return Math.min(possibleMinDrawerWidth, maxDrawerWidth);
    }


    //Check if we should adjust our layouts for tablets
    public boolean useTabletMenu() {
        return (getResources().getBoolean(R.bool.isWideTablet) && TABLET_LAYOUT);
    }
}