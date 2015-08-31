package tk.easthigh.witsmobile.tools;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import tk.easthigh.witsmobile.R;

/**
 * Created by Suleiman on 14-04-2015.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.VersionViewHolder> {

    public List<String> ActivitiesList= null;
    public List<String> ActivitiesID= null;
    public List<String> ActivitiesSubList = null;
    public List<String> ActivitiesSubList2 = null;
    public List<String> ActivitiesTag = null;
    Context context;
    OnItemClickListener clickListener;
    private static final Pattern DOUBLE_PATTERN = Pattern.compile(
            "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" +
                    "([eE][+-]?(\\p{Digit}+))?)|(\\.(\\p{Digit}+)([eE][+-]?(\\p{Digit}+))?)|" +
                    "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" +
                    "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");

    public String[] titles;
    public String[] subTitles;
    public String[] subTitles2;

    public RecyclerAdapter() {
        setActivitiesList();
    }

    public void setActivitiesList() {
        if (ActivitiesList == null) {
            ActivitiesList = new ArrayList<>();
            ActivitiesSubList = new ArrayList<>();
            ActivitiesSubList2 = new ArrayList<>();
            ActivitiesID = new ArrayList<>();
            ActivitiesTag = new ArrayList<>();
        }
    }

    public static boolean isFloat(String s) {
        return DOUBLE_PATTERN.matcher(s).matches();
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new VersionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VersionViewHolder versionViewHolder, int i) {
            if (ActivitiesList.size() != 0)
                versionViewHolder.title.setText(ActivitiesList.get(i));
            if (ActivitiesSubList.size() != 0)
                versionViewHolder.subTitle.setText(ActivitiesSubList.get(i));
            if (ActivitiesSubList2.size() != 0)
                versionViewHolder.subTitle2.setText(ActivitiesSubList2.get(i));
            if (ActivitiesID.size() != 0)
                versionViewHolder.id.setText(ActivitiesID.get(i));

    }

    public ArrayList<String> getActivitiesTag(){
        return (ArrayList<String>) ActivitiesTag;
    }

    @Override
    public int getItemCount() {
        return ActivitiesList == null ? 0 : ActivitiesList.size();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardItemLayout;
        TextView title;
        TextView subTitle;
        TextView subTitle2;
        TextView id;

        public VersionViewHolder(View itemView) {
            super(itemView);
            cardItemLayout = (CardView) itemView.findViewById(R.id.list_item);
            title = (TextView) itemView.findViewById(R.id.listitem_name);
            subTitle = (TextView) itemView.findViewById(R.id.listitem_subname);
            subTitle2 = (TextView) itemView.findViewById(R.id.listitem_subname2);
            id = (TextView) itemView.findViewById(R.id.listitem_id);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

}
