package tk.easthigh.witsmobile.tools;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import tk.easthigh.witsmobile.R;

/**
 * A complete rewrite, because I can't even.
 */
public class RssFragmentBase extends Fragment {

    private final String LOG_TAG = "RssFragmentBase";

    protected RecyclerView recyclerView;
    private Activity activity;
    private View progressView;

    public RssFragmentBase() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        Context context = activity.getApplicationContext();
        View view = inflater.inflate(R.layout.fragment_rss, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.home_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        progressView = view.findViewById(R.id.home_progress);

        initRss();

        return view;
    }

    /**
     * Shows the progress UI and hides the recyclerview.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = activity.getResources().getInteger(android.R.integer.config_shortAnimTime);

            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            recyclerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void initRss() {
        RunnableFuture runnable = new FutureTask<>(new Callable<Boolean>() {

            @Override
            public Boolean call() {
                DataManager dataManager = new DataManager();
                SharedPreferences sharedPreferences = (activity != null) ? activity.getSharedPreferences("rssPreferences", Context.MODE_PRIVATE) : null;
                if (sharedPreferences == null)
                    Log.wtf(LOG_TAG, "Activity not found!");

                // Display message if no internet is found; otherwise, it tries to get the feed
                if (!dataManager.isOnline()) {
                    Snackbar.make(activity.findViewById(R.id.nav_contentframe), "No internet connection!", Snackbar.LENGTH_SHORT);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getView().findViewById(R.id.noInternet) != null)
                                getView().findViewById(R.id.noInternet).setVisibility(View.VISIBLE);
                            showProgress(false);
                        }
                    });
                    return false;
                }
                return true;
            }

        });

        new Thread(runnable).start();
        try {
            if (!(Boolean) runnable.get())
                Log.e(LOG_TAG, "No Internet Connection!");
        } catch (ExecutionException | InterruptedException e) {
            Log.wtf(LOG_TAG, "Something went very wrong!");
        }
    }

    /**
     * Checks the first title of the Rss feed. If they're not the same, then there needs to be an update.
     * The logic for only checking the first one is that posts are usually not updated post-post. As a
     * result, if there's a new post, then usually the rest of the set is different. Thus, it's a waste
     * of cpu to check the rest.
     * @param sharedPreferences SharedPreferences used to cache strings.
     * @param compString Title of the first RSS item.
     * @return true if the cached title != comp string.
     */
    protected boolean doesRssNeedUpdate(SharedPreferences sharedPreferences, String compString) {
        if (compString.length() > 40)
            compString = compString.substring(0, 38) + "…";
        Set<String> tempSet = sharedPreferences.getStringSet("rssTitles", new HashSet<String>(0));
        String cachedString;

        if (tempSet.size() != 0) {
            Log.i(LOG_TAG, "Cached data found!");
            cachedString = tempSet.toArray(new String[tempSet.size()])[0];
            Log.v(LOG_TAG, "Comparison String: " + compString);
            Log.v(LOG_TAG, "Cached String: " + cachedString);

        }
        else {
            Log.w(LOG_TAG, "No Cached data!");
            return true;
        }

        return !cachedString.equals(compString);
    }

    protected void populate() {
        Log.e(LOG_TAG, "Called RSSFragmentBase populate, please fix!");
    }
}
