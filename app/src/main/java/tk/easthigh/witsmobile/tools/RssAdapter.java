package tk.easthigh.witsmobile.tools;

import java.util.Arrays;

/**
 * Created by Edward on 7/15/2015.
 */
public class RssAdapter extends RecyclerAdapter {


    @Override
    public void setActivitiesList() {
        super.setActivitiesList();

        if (titles != null && titles.length != 0) {
            ActivitiesList = Arrays.asList(titles);
            ActivitiesSubList = Arrays.asList(subTitles);
            ActivitiesSubList2 = Arrays.asList(subTitles2);
        }
    }


    public RssAdapter(String[] titles, String[] subTitles, String[] subTitles2) {
        this.titles = titles;
        this.subTitles = subTitles;
        this.subTitles2 = subTitles2;
        setActivitiesList();
    }
}
