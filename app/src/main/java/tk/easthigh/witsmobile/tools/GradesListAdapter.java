package tk.easthigh.witsmobile.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;

/**
 * Created by Edward on 7/15/2015.
 */
public class GradesListAdapter extends RecyclerAdapter {

    public GradesListAdapter(Context context, int pos) {
        this.context = context;
        setActivitiesList(Integer.toString(pos));
    }

    public void setActivitiesList(String pos) {
        super.setActivitiesList();

        SecurePreferences sPrefs = new DataManager().getSecurePrefs(context);

        for(int i = 0; i < Integer.valueOf(sPrefs.getString(pos + ".numQuarters")); i++) {
            String quarter = Integer.toString(i);

            ActivitiesList.add("Quarter " + Integer.toString(i + 1));
            ActivitiesSubList.add("Entries:  " + sPrefs.getString(pos + ".quarter" + quarter + ".numEntries"));
            ActivitiesSubList2.add("");
            ActivitiesID.add(sPrefs.getString(pos + ".quarter" + Integer.toString(i) + ".quarterGrade") + "%");
            ActivitiesTag.add("quarter" + Integer.toString(i));

            for (int x = 0; x < Integer.valueOf(sPrefs.getString(pos + ".quarter" + quarter + ".numEntries")); x++){
                String entry = Integer.toString(x);

                ActivitiesList.add(sPrefs.getString(pos + ".quarter" + quarter + ".entry" + entry + ".title"));

                ActivitiesSubList.add(sPrefs.getString(pos + ".quarter" + quarter + ".entry" + entry + ".type"));

                ActivitiesSubList2.add(
                        sPrefs.getString(pos + ".quarter" + quarter + ".entry" + entry + ".ptsGot") +
                                " / " +
                                sPrefs.getString(pos + ".quarter" + quarter + ".entry" + entry + ".ptsTotal")
                );

                if (sPrefs.getString(pos + ".quarter" + quarter + ".entry" + entry + ".ptsGot").equals("M"))
                    ActivitiesID.add("Missing");
                else if (!isFloat(sPrefs.getString(pos + ".quarter" + quarter + ".entry" + entry + ".ptsGot")))
                    ActivitiesID.add(sPrefs.getString(pos + ".quarter" + quarter + ".entry" + entry + ".ptsGot"));
                else {
                    float grade = Float.valueOf(sPrefs.getString(pos + ".quarter" + quarter + ".entry" + entry + ".ptsGot")) /
                            Float.valueOf(sPrefs.getString(pos + ".quarter" + quarter + ".entry" + entry + ".ptsTotal"));

                    String gradeToString = Integer.toString(Math.round(grade * 100)) + "%";
                    ActivitiesID.add(gradeToString);
                }
                ActivitiesTag.add("");
            }
        }
    }

    @Override
    public void onBindViewHolder(VersionViewHolder versionViewHolder, int i) {
        super.onBindViewHolder(versionViewHolder, i);

        int px = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, Resources.getSystem().getDisplayMetrics()) + 0.5f);

        if (!ActivitiesTag.get(i).equals("")) {
            versionViewHolder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
            versionViewHolder.id.setTextSize(TypedValue.COMPLEX_UNIT_SP, 68);
            versionViewHolder.id.setTextColor(Color.BLACK);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) versionViewHolder.cardItemLayout.getLayoutParams();
            layoutParams.height = px * 150; // FIXME: id
            layoutParams.setMargins(px * 8, px * 8, px * 8, px * 4); // TODO: shouldn't these be px * 16?
            versionViewHolder.cardItemLayout.setLayoutParams(layoutParams);
        } else {
            // I'm an idiot.
            versionViewHolder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            versionViewHolder.id.setTextSize(TypedValue.COMPLEX_UNIT_SP, 42);
            versionViewHolder.id.setTextColor(Color.parseColor("#8a000000"));
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) versionViewHolder.cardItemLayout.getLayoutParams();
            layoutParams.height = px * 100; // FIXME: id
            layoutParams.setMargins(px * 8, 0, px * 8, px * 2);
            versionViewHolder.cardItemLayout.setLayoutParams(layoutParams);
        }
    }
}
