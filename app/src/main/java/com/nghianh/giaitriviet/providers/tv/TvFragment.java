package com.nghianh.giaitriviet.providers.tv;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nghianh.giaitriviet.JCVideoPlayerAdmob;
import com.nghianh.giaitriviet.R;
import com.nghianh.giaitriviet.activity.TVActivity;
import com.nghianh.giaitriviet.model.TVChannel;
import com.nghianh.giaitriviet.util.Helper;
import com.squareup.picasso.Picasso;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;


/**
 * This fragment is used to play live video streams.
 */
public class TvFragment extends Fragment {

    protected TVChannel tvChannel;
    private TVActivity mAct;
    private RelativeLayout rl;
    private JCVideoPlayerAdmob jcVideoPlayerStandard;

    /**
     * Called when the activity is first created.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!(getActivity() instanceof TVActivity)) throw new AssertionError();

        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_tv, container, false);
        jcVideoPlayerStandard = (JCVideoPlayerAdmob) rl.findViewById(R.id.custom_videoplayer_standard);
        FloatingActionButton fab = (FloatingActionButton) rl.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View v = view;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Get the layout inflater
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_signin, null);
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(dialogView)
                        // Add action buttons
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText edit = (EditText) dialogView.findViewById(R.id.mailAdd);
                                String text = edit.getText().toString();
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setData(Uri.parse("mailto:"));
                                i.setType("message/rfc822");
                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"huunghia.it11@gmail.com"});
                                i.putExtra(Intent.EXTRA_SUBJECT, "Report TV died link");
                                i.putExtra(Intent.EXTRA_TEXT, "From: " + text + "\n" + "This TV link died: " + tvChannel.getStreamUrl());
                                try {
                                    startActivity(Intent.createChooser(i, "Send mail..."));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                }
                                Snackbar.make(v, "Thanks for you report:" + text, Snackbar.LENGTH_LONG)
                                        .setAction("OK", null).show();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Snackbar.make(v, "Your report will help us improve TV service", Snackbar.LENGTH_LONG)
                                        .setAction("Cancel", null).show();
                            }
                        });
                builder.create();
                builder.show();
            }
        });
        setHasOptionsMenu(true);
        return rl;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAct = (TVActivity) getActivity();
        Helper.isOnline(mAct, true);
        playTV(tvChannel);
    }

    public void playTV(TVChannel tvChannel) {
        jcVideoPlayerStandard.setUp(tvChannel.getStreamUrl(), tvChannel.getChannelName());
        Picasso.with(getContext())
                .load(tvChannel.getImgUrl())
                .placeholder(R.drawable.play_icon)
                .resize(640, 640)
                .into(jcVideoPlayerStandard.thumbImageView);
        //jcVideoPlayerStandard.startButton.performClick();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        /*final String tv_chanels = this.getArguments().getStringArray(MainActivity.FRAGMENT_DATA)[1];
        final String[] lblTVName = Arrays.copyOf(ListTV.get(Integer.parseInt(tv_chanels)).keySet().toArray(), ListTV.get(Integer.parseInt(tv_chanels)).size(), String[].class);
        final String[] lblTVUrl = Arrays.copyOf(ListTV.get(Integer.parseInt(tv_chanels)).values().toArray(), ListTV.get(Integer.parseInt(tv_chanels)).size(), String[].class);
        inflater.inflate(R.menu.tv_menu, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        final Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<String> adapter = null;

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lblTVName);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
                //toolbar.setTitle(spinner.getSelectedItem().toString());
                currentTV = lblTVUrl[position];
                jcVideoPlayerStandard.setUp(lblTVUrl[position], spinner.getSelectedItem().toString());
                jcVideoPlayerStandard.thumbImageView.setImageResource(R.drawable.ic_launcher);
                jcVideoPlayerStandard.startButton.performClick();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        JCVideoPlayer.releaseAllVideos();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        JCVideoPlayer.releaseAllVideos();
        super.onPause();
    }


}


