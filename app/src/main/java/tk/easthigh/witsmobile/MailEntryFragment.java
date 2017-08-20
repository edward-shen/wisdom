package tk.easthigh.witsmobile;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import tk.easthigh.witsmobile.tools.MailEntryAdapter;
import tk.easthigh.witsmobile.tools.RecyclerAdapter;

public class MailEntryFragment extends Fragment {

    private View view;
    private ProgressBar progressView;
    private RecyclerView recyclerView;
    private Context context;
    private RecyclerAdapter adapter;

    public MailEntryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Activity activity = getActivity();
        view = inflater.inflate(R.layout.fragment_mail_entry, container, false);
        progressView = (ProgressBar) view.findViewById(R.id.mail_entry_progress);
        recyclerView = (RecyclerView) view.findViewById(R.id.mail_entry_recyclerview);
        context = getActivity().getApplicationContext();

        showProgress(true);

        setUpRecyclerView();

        return view;
    }

    private void setUpRecyclerView(){
        recyclerView = (RecyclerView) view.findViewById(R.id.mail_entry_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (adapter == null) {
            adapter = new MailEntryAdapter(context, getArguments().getInt("pos"));
            recyclerView.setAdapter(adapter);
        }
    }


    /**
     * Shows the progress UI and hides the mail form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            recyclerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                    // TODO: Bug: When logging in and switching to another fragment, a illegal state exception is thrown, since the fragment isn't attached to the activity
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

}
