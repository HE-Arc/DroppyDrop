package android.hearc.ch.droppydrop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;
import android.widget.TextView;

public class ScoreAdapter extends BaseAdapter {

    private Context context;
    private List<String> stringList;

    public ScoreAdapter(Context context) {
        super();

        this.context = context;
        stringList = new ArrayList<String>();
    }

    public void add(String value) {
        stringList.add(value);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return stringList.size();
    }

    @Override
    public Object getItem(int position) {
        return stringList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScoreHolder holder;

        if(context == null){
            convertView = LayoutInflater.from(context).inflate(null, parent, false);
            holder = new ScoreHolder();
            holder.valueTextView = (TextView) convertView.findViewById(R.id.valueTextView);
            convertView.setTag(holder);
        } else {
            holder = (ScoreHolder) convertView.getTag();
        }

        holder.valueTextView.setText(stringList.get(position));

        return convertView;
    }

    private static class ScoreHolder {
        TextView valueTextView;
    }
}
