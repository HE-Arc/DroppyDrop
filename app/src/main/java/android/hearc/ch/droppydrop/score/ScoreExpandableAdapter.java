package android.hearc.ch.droppydrop.score;

import android.content.Context;
import android.graphics.Typeface;
import android.hearc.ch.droppydrop.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * https://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
 */
public class ScoreExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ScoreManager scoreManager;

    public ScoreExpandableAdapter(Context context, ScoreManager scoreManager) {
        super();
        this.context = context;
        this.scoreManager = scoreManager;
    }

    @Override
    public int getGroupCount() {
        return scoreManager.getAllScores().size();
    }

    @Override
    public int getChildrenCount(int levelId) {
        Set sc = scoreManager.getScoresByLevel(levelId);
        if(sc != null)
            return scoreManager.getScoresByLevel(levelId).size();
        else
            return 0;
    }

    @Override
    public Object getGroup(int levelId) {
        return scoreManager.getScoresByLevel(levelId);
    }

    @Override
    public Object getChild(int levelId, int ranking) {
        return scoreManager.getScoresByLevel(levelId).toArray()[ranking];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = "Level " + groupPosition;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        SortedSet<Score> scoreLevel = scoreManager.getScoresByLevel(groupPosition);
        String childText = "rank " + childPosition;
        if(scoreLevel != null)
            childText = scoreLevel.toArray()[childPosition].toString();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
