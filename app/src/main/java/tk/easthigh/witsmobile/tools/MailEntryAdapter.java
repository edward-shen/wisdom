package tk.easthigh.witsmobile.tools;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Edward on 7/18/2015.
 */
public class MailEntryAdapter extends RecyclerAdapter {
    private int pos;

    public MailEntryAdapter(Context context, int pos){
        this.context = context;
        this.pos = pos;
        setActivitiesList();
    }

    @Override
    public void setActivitiesList() {
        super.setActivitiesList();
        SecurePreferences sPrefs = new DataManager().getSecurePrefs(context);
        new DataManager(sPrefs).setMailEntry(pos);
    }
}
