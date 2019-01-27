package android.hearc.ch.droppydrop.score;

import android.content.Context;
import android.graphics.Typeface;
import android.hearc.ch.droppydrop.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * https://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
 */
public class ScoreExpandableAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "SCORE_ADAPTER";

    private Context context;
    private List<String> listLevelHeader;
    private HashMap<String, List<Score>> mapScoreChild;


    public ScoreExpandableAdapter(Context context, ScoreManager scoreManager) {
        super();
        this.context = context;
        listLevelHeader = new ArrayList<>();
        mapScoreChild = new HashMap<>();
        readAllFrom(scoreManager);
    }

    private void readAllFrom(ScoreManager sm)
    {
        Map<Integer, SortedSet<Score>> mapScore = sm.getAllScores();
        for(Map.Entry<Integer, SortedSet<Score>> entry : mapScore.entrySet())
        {
            String levelHeader = "Level " + entry.getKey().intValue();
            listLevelHeader.add(0, levelHeader);
            SortedSet<Score> scores = entry.getValue();
            List<Score> listScore = new ArrayList<>(scores);
            Collections.reverse(listScore);
            mapScoreChild.put(levelHeader, listScore);
        }
    }

    /* Group : SortedSet<Score> */
    @Override
    public Object getGroup(int groupPosition) {
        return this.mapScoreChild.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mapScoreChild.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = listLevelHeader.get(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    /* Child : Score */

    @Override
    public int getChildrenCount(int groupPosition) {
        return mapScoreChild.get(listLevelHeader.get(groupPosition)).size();
    }

    @Override
    public Score getChild(int groupPosition, int childPosititon) {
        return mapScoreChild.get(listLevelHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Score childScore = getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtPlayerChild = (TextView) convertView.findViewById(R.id.lblPlayerName);
        txtPlayerChild.setText(childScore.username);

        TextView txtValueChild = (TextView) convertView.findViewById(R.id.lblScorePoints);
        txtValueChild.setText(childScore.value + " pts");

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
