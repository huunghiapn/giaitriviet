package com.nghianh.giaitriviet.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.nghianh.giaitriviet.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by bado on 5/29/15.
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = BaseFragment.class.getSimpleName();

    protected ProgressDialog progress;

    public abstract int getResourceLayout();

    public void initViews(View view) {
        Log.e(TAG, "initView ");
    }

    public void attachListeners() {
    }

    public void loadData() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getResourceLayout(), container, false);

        Log.e(TAG, "onCreate View initView ");
        initViews(view);
        initActionBar(view);

        progress = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        attachListeners();
    }

    public void initActionBar(View root) {

    }

    @Override
    public void onClick(View view) {

    }

    /*public void loadFragment(Fragment fragment){
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();
        }
    }*/

    public void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

//    public void showErrorDataLoading(){
//        DialogUtils.showConfirmDialogOne(getActivity(), getString(R.string.dialog_error),  getString(R.string.dialog_login_require), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//            }
//        });
//    }
//
//    public void showErrorLogin(){
//        DialogUtils.showConfirmDialog(getActivity(), getString(R.string.dialog_login_require), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Intent intent = new Intent(getActivity(), LoginActivity.class);
//                startActivity(intent);
//            }
//        }, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//    }
}