package tk.easthigh.witsmobile;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import tk.easthigh.witsmobile.tools.GradesListAdapter;
import tk.easthigh.witsmobile.tools.RecyclerAdapter;

public class GradesFragment extends Fragment {

    private RecyclerAdapter adapter;

    public GradesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_classes, container, false);
        Context context = getActivity().getApplicationContext();

        setUpRecyclerView(view, context);

        return view;
    }

    private void setUpRecyclerView(View view, Context context) {

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.classes_recyclerview);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (adapter == null) {
            adapter = new GradesListAdapter(context, getArguments().getInt("class"));
            recyclerView.setAdapter(adapter);
        }

        final ArrayList<String> activitiesTag = adapter.getActivitiesTag();

        adapter.SetOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            // TODO: Add functionality to header of each quarter
            public void onItemClick(View view, int position) {
                Log.v("GradesFragment", "activitiesTag at this pos contains=" + activitiesTag.get(position));
                if (!activitiesTag.get(position).equals("")){
                    Log.i("GradesFragment", "Magically removes the entries of this quarter! If only...");
                } else
                    Log.i("GradesFragment", "Hey, this didn't do anything! Good!");
            }
        });
    }

}
