package tk.easthigh.witsmobile;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tk.easthigh.witsmobile.tools.ClassAdapter;
import tk.easthigh.witsmobile.tools.DataManager;
import tk.easthigh.witsmobile.tools.RecyclerAdapter;
import tk.easthigh.witsmobile.tools.SecurePreferences;

public class ClassesFragment extends Fragment {

    private RecyclerAdapter adapter; // TODO: Is putting this as a field necessary?
    private Activity activity;
    private RecyclerView recyclerView;
    private SecurePreferences sPrefs;

    public ClassesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classes, container, false);
        activity = getActivity();
        Context context = activity.getApplicationContext();
        sPrefs = new DataManager().getSecurePrefs(context);
        setUpRecyclerView(view, context);

        if (sPrefs.getString("ClassesFragmentLastPosition") != null)
            recyclerView.scrollToPosition(Integer.valueOf(sPrefs.getString("ClassesFragmentLastPosition")));

        // Inflate the layout for this fragment
        return view;
    }

    private void setUpRecyclerView(View view, Context context) {

        recyclerView = (RecyclerView) view.findViewById(R.id.classes_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (adapter == null) {
            adapter = new ClassAdapter();
            recyclerView.setAdapter(adapter);
        }

        adapter.SetOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            int position;
            @Override
            public void onItemClick(View view, int position) {

                LinearLayoutManager llm = (LinearLayoutManager) (recyclerView.getLayoutManager());
                // TODO: find a way to round the location; if the top visible thing has >50% showing, round to findFirstVisibleItemPosition, else round to findFirstCompletelyVisibleItemPosition
                this.position = llm.findFirstVisibleItemPosition();

                Fragment fragment = null;

                for (int i = 0; i < Integer.valueOf(sPrefs.getString("numClasses")); i++)
                    if (position == i) {
                        Log.d("numQuarters", sPrefs.getString(i + ".numQuarters"));
                        if (sPrefs.getString(i + ".numQuarters").equals("0"))
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(activity.findViewById(R.id.nav_contentframe), "This class has no grades!", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        else {
                            Bundle bdl = new Bundle(1);
                            bdl.putInt("class", i);
                            fragment = new GradesFragment();
                            fragment.setArguments(bdl);
                        }
                    }

                if (fragment != null) {
                    sPrefs.put("ClassesFragmentLastPosition", Integer.toString(this.position));
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right)
                            .replace(R.id.nav_contentframe, fragment, null)
                            .commit();
                    sPrefs.put("parentFragment", "ClassesFragment");
                }

                Log.d("Backstack", Integer.toString(getFragmentManager().getBackStackEntryCount()));
            }
        });
    }

}