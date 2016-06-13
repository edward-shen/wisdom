package tk.easthigh.witsmobile;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import tk.easthigh.witsmobile.rss.RssFeed;
import tk.easthigh.witsmobile.rss.RssItem;
import tk.easthigh.witsmobile.rss.RssReader;
import tk.easthigh.witsmobile.tools.DataManager;
import tk.easthigh.witsmobile.tools.RecyclerAdapter;
import tk.easthigh.witsmobile.tools.RssAdapter;
import tk.easthigh.witsmobile.tools.RssFragmentBase;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends RssFragmentBase {


    private final String LOG_TAG = "homeFragment";
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private RssAdapter adapter;
    private String[] rssTitles;
    private String[] rssDesc;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);


        activity = getActivity();
        sharedPreferences =  activity.getSharedPreferences("homePageRSS", Context.MODE_PRIVATE);
/*
        if (!sharedPreferences.getBoolean("isCached", false)) {
            if (new DataManager().isOnline()) {
                showProgress(true);
                try {
                    parseRSS(new URL("http://williamsvillewisdom.wordpress.com/feed/"));
                } catch (MalformedURLException e) {
                    Log.wtf(LOG_TAG, "http://williamsvillewisdom.wordpress.com/feed/ returned malformed URL!?");
                }
            }
        } else {
            populate(); // TODO: Finish
        } */

        return view;
    }


    private void parseRSS(final URL url) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                RssFeed feed = null;
                ArrayList<RssItem> rssItems;
                DataManager dataManager = new DataManager();
                boolean cacheRequiresUpdate;

                try {
                    feed = RssReader.read(url);
                } catch (SAXException | IOException e) {
                    e.printStackTrace();
                }

                assert activity != null : Log.wtf(LOG_TAG, "activity == null!");

                for (int i = 0; i < feed.getRssItems().size(); i++)
                    Log.v(LOG_TAG, "Feed item " + i + ": " + feed.getRssItems().get(i).getTitle());

                // FIXME: cache isn't working
                String comparisonString = feed.getRssItems().get(0).getTitle();

                cacheRequiresUpdate = doesRssNeedUpdate(sharedPreferences, comparisonString);

                // More logs can't hurt
                Log.v(LOG_TAG, "Does cache require update? " + cacheRequiresUpdate);

                if (!cacheRequiresUpdate) { // Online and cache exists
                    Log.i(LOG_TAG, "Attempting to populate from cache...");

                    if (sharedPreferences.getStringSet("rssTitles", new HashSet<String>(0)).size() != 0) {
                        /* The next block defines each array by reusing the array tempSet
                         * The reason for using a temp set instead of directly calling each data set is because
                         * otherwise each declaration would be essentially unreadable and extremely long.
                         */
                        Set tempSet = sharedPreferences.getStringSet("rssTitles", new HashSet<String>(0));
                        rssTitles = ((String[]) tempSet.toArray(new String[tempSet.size()]));

                        tempSet = sharedPreferences.getStringSet("rssDesc", new HashSet<String>(0));
                        rssDesc = ((String[]) tempSet.toArray(new String[tempSet.size()]));

                        tempSet.clear();

                        sharedPreferences.edit().putBoolean("isCached", true); //TODO: Is necessary?

                        populate();
                        Log.i(LOG_TAG, "Population from cache was successful!");
                    } else
                        Log.i(LOG_TAG, "No Cache Data!");

                } else if (feed != null) { // Online and feed is ready

                    Log.i(LOG_TAG, "Extracting data from feed...");

                    rssItems = feed.getRssItems();
                    rssTitles = new String[rssItems.size()];
                    rssDesc = new String[rssItems.size()];

                    // Clears the cache
                    sharedPreferences.edit().clear().apply();

                    for (int i = 0; i < rssItems.size(); i++) {

                        rssTitles[i] = (rssItems.get(i).getTitle().length() > 40) ?
                                rssItems.get(i).getTitle().substring(0, 38) + "…" :
                                rssItems.get(i).getTitle();

                        rssDesc[i] = rssItems.get(i).getDescription().trim();
                        if (rssDesc[i].length() > 90)
                            rssDesc[i] = rssDesc[i].trim().substring(0, 88) + "…"; // Assuming that the … character takes up at least 2 spaces


                    }

                    String[] setIdentifiers = {"rssTitles", "rssDesc"};

                    Log.i(LOG_TAG, "Caching data...");
                    if (dataManager.verifyRssCache("eastSideRSS", rssItems.size(), true, setIdentifiers, rssTitles, rssDesc)) {
                        sharedPreferences.edit().putStringSet("rssTitles", new HashSet<>(Arrays.asList(rssTitles))).apply();
                        // FIXME Why is this not equal in size as all other sets
                        sharedPreferences.edit().putStringSet("rssDesc", new HashSet<>(Arrays.asList(rssDesc))).apply();
                        Log.i(LOG_TAG, "Caching successful!");
                    } else
                        Log.e(LOG_TAG, "RSS verification failed, not caching!");

                    populate();
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getView().findViewById(R.id.noInternet).setVisibility(View.VISIBLE);
                            showProgress(false);
                        }
                    });
                }
            }
        }).start();
    }

    protected void populate(){

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                adapter = new RssAdapter(rssTitles, rssDesc,  new String[0]);
                recyclerView.setAdapter(adapter);
                recyclerView.invalidate();
                adapter.SetOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO: something
                    }
                });
                showProgress(false);
            }
        });
    }
}
