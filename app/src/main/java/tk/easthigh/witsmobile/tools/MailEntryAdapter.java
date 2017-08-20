package tk.easthigh.witsmobile.tools;

import android.content.Context;

/**
 * Created by Edward on 7/18/2015.
 */
public class MailEntryAdapter extends RecyclerAdapter {

    public MailEntryAdapter(Context context, int pos){
        setActivitiesList(context, pos);
    }

    public void setActivitiesList(Context context, int pos) {
        super.setActivitiesList();
        SecurePreferences sPrefs = new DataManager().getSecurePrefs(context);
        new DataManager(sPrefs).setMailEntry(pos);
    }
}
