package com.nghianh.giaitriviet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nghianh.giaitriviet.R;
import com.nghianh.giaitriviet.adapter.ImageAdapter;
import com.nghianh.giaitriviet.model.TVChannel;
import com.nghianh.giaitriviet.providers.tv.TvFragment;
import com.nghianh.giaitriviet.util.Helper;

import java.util.ArrayList;

public class TVActivity extends AppCompatActivity {

    public static final String ITEM_LIST = "tvchannel";
    public static final String ITEM_POS = "channelpos";
    private TVChannel tvChannel;
    private Integer pos;
    private ArrayList<TVChannel> listViewItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);
        pos = getIntent().getIntExtra(ITEM_POS, 0);
        listViewItems = (ArrayList<TVChannel>) getIntent().getSerializableExtra(ITEM_LIST);
        tvChannel = listViewItems.get(pos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tvChannel.getChannelName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(pos + 1, tvChannel))
                .commit();

        final GridView gridview = (GridView) findViewById(R.id.gridview);
        ArrayList<TVChannel> listViewItemTmp = new ArrayList<>();
        listViewItemTmp.addAll(listViewItems);
        listViewItemTmp.remove(tvChannel);
        gridview.setAdapter(new ImageAdapter(getApplicationContext(), tvChannel.getChannelName(), listViewItemTmp));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TVActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra(TVActivity.ITEM_LIST, listViewItems);
                intent.putExtra(TVActivity.ITEM_POS, position);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
        Helper.admobLoader(this, getResources(), findViewById(R.id.adView));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tv, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:

                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends TvFragment {
        public static final String EXTRA_ITEM = "tvchannel";
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, TVChannel tvChannel) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putSerializable(EXTRA_ITEM, tvChannel);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            /*View rootView = inflater.inflate(R.layout.fragment_tv2, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/
            tvChannel = (TVChannel) getArguments().getSerializable(EXTRA_ITEM);
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
}
