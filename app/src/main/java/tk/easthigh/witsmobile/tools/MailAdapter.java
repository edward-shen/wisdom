package tk.easthigh.witsmobile.tools;

import tk.easthigh.witsmobile.MainActivity;

/**
 * Created by Edward on 7/15/2015.
 */
public class MailAdapter extends RecyclerAdapter {

    @Override
    public void setActivitiesList() {
        super.setActivitiesList();
        SecurePreferences sPrefs = MainActivity.preferences;
        for (int i = 0; i < Integer.valueOf(sPrefs.getString("mail.size")); i++){
            ActivitiesList.add(sPrefs.getString("mail" + Integer.toString(i) + ".title"));
            ActivitiesSubList.add(sPrefs.getString("mail" + Integer.toString(i) + ".sender"));
            ActivitiesSubList2.add(sPrefs.getString("mail" + Integer.toString(i) + ".date"));
        }
    }
}
