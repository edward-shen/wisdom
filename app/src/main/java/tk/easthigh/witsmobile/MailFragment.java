package tk.easthigh.witsmobile;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tk.easthigh.witsmobile.tools.DataManager;
import tk.easthigh.witsmobile.tools.MailAdapter;
import tk.easthigh.witsmobile.tools.RecyclerAdapter;
import tk.easthigh.witsmobile.tools.SecurePreferences;


/**
 * A simple {@link Fragment} subclass.
 */
public class MailFragment extends Fragment {

    private Activity activity;
    private View progressView;
    private RecyclerAdapter adapter; // TODO: Is putting this as a field necessary?
    private RecyclerView recyclerView;
    private View view;
    private Context context;
    private SecurePreferences sPrefs;

    public MailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = getActivity();
        context = activity.getApplicationContext();
        view = inflater.inflate(R.layout.fragment_mail, container, false);
        progressView = activity.findViewById(R.id.mail_progress);
        sPrefs = new DataManager().getSecurePrefs(context);
        setUpRecyclerView();
        // Inflate the layout for this fragment
        return view;
    }

    private void setUpRecyclerView(){
        recyclerView = (RecyclerView) view.findViewById(R.id.mail_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (adapter == null) {
            adapter = new MailAdapter();
            recyclerView.setAdapter(adapter);
        }

        adapter.SetOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Fragment fragment = new MailEntryFragment();
                Bundle bdl = new Bundle();
                bdl.putInt("pos", position);
                fragment.setArguments(bdl);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right)
                        .replace(R.id.nav_contentframe, fragment, "currentFragment")
                        .commit();
                sPrefs.put("parentFragment", "MailFragment");
            }


        });
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
