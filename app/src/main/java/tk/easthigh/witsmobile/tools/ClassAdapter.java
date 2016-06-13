package tk.easthigh.witsmobile.tools;

import tk.easthigh.witsmobile.MainActivity;

/**
 * Created by Edward on 7/15/2015.
 */
public class ClassAdapter extends RecyclerAdapter {

    public ClassAdapter() { }

    @Override
    public void setActivitiesList() {
        super.setActivitiesList();

        for(int i = 0; i < Integer.valueOf(MainActivity.preferences.getString("numClasses")); i++){
            ActivitiesList.add(MainActivity.preferences.getString(Integer.toString(i) + ".class"));
            ActivitiesSubList.add(MainActivity.preferences.getString(Integer.toString(i) + ".teacher"));
            if (MainActivity.preferences.getString(Integer.toString(i) + ".day").equals("AB"))
                ActivitiesID.add(MainActivity.preferences.getString(Integer.toString(i) + ".period"));
            else
                ActivitiesID.add(MainActivity.preferences.getString(Integer.toString(i) + ".period") +  MainActivity.preferences.getString(Integer.toString(i) + ".day"));

            int currentAvg = -1;
            int totalClassAvg = 0;
            int counter = 0;
            for (int x = 3; x >= 0; x--){
                // TODO: Switch data method collection from quarter grades to schedule grades
                // FIXME: Not always will this work.
                if (MainActivity.preferences.getString(Integer.toString(i) + ".quarter" + Integer.toString(x) + ".quarterGrade") != null){
                    if (currentAvg == -1)
                        currentAvg = Integer.valueOf(MainActivity.preferences.getString(Integer.toString(i) + ".quarter" + Integer.toString(x) + ".quarterGrade"));
                    totalClassAvg += Integer.valueOf(MainActivity.preferences.getString(Integer.toString(i) + ".quarter" + Integer.toString(x) + ".quarterGrade"));
                    counter++;
                }
            }

            if (currentAvg == -1) {
                ActivitiesSubList2.add("");
            } else
                ActivitiesSubList2.add("Current Average: " + currentAvg + "\nOverall Class Average: " + totalClassAvg/counter);

        }

    }
}
