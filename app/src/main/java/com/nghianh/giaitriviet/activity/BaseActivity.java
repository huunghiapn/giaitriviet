package com.nghianh.giaitriviet.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.nghianh.giaitriviet.R;
import com.nghianh.giaitriviet.util.GeneralUtils;

/**
 * Created by NghiaNH on 3/2/2017.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BaseActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    protected Toolbar mToolBar;
    protected ProgressDialog progress;

    public abstract int getResourceLayout();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void attachListeners() {

    }

    ;

    public void initViews() {

    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResourceLayout());
        initialFistLoad();
        initViews();
        doSetupAfterRenderedView();
        attachListeners();

        progress = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
    }

    public void setTitle(String title) {
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
    }

    /**
     * The configuration for activity when activity start
     */
    public void initialFistLoad() {

    }

    /**
     * Setup view, after init
     */
    public void doSetupAfterRenderedView() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {

    }

    protected void setLoadingMesasge(int id) {
        progress.setMessage(getString(id));
    }


    protected void showLoading() {
        if (!progress.isShowing() && GeneralUtils.isNetworkOnline(this)) {
            progress.show();
        }
    }

    protected void hideLoading() {
        if (progress.isShowing()) {
            progress.dismiss();
        }
    }

    public void loadFragment(Fragment fragment, int resId) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(resId, fragment).commit();
        }
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    public void checkPermission() {
        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    PERMISSION_REQUEST_CODE);
        }
    }

    public void showToastMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
                .show();
    }
}
