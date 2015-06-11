package ceorganizer.oved.gilad.ceorganizer;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gilad on 6/4/15.
 */
public class MyArrayAdapter extends ParseQueryAdapter {
    public MyArrayAdapter(Context context, final String caller) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Summary");
                query.addDescendingOrder("createdAt");
                query.whereEqualTo("caller", caller);
                return query;
            }
        });
    }

    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.eventcell, null);
        }
        super.getItemView(object, v, parent);

        // Add the title view
        TextView titleTextView = (TextView) v.findViewById(R.id.summaryLbl);
        String summary = object.getString("summary");
        String name = object.getString("caller");
        titleTextView.setText(summary);

        String type = object.getString("type");
        if (type.equals("incoming")) {
            titleTextView.setTextColor(Color.BLUE);
        } else {
            titleTextView.setTextColor(Color.rgb(255, 102, 0));
        }
        titleTextView.setTextSize(16);

        // Add a reminder of how long this item has been outstanding
        TextView timestampView = (TextView) v.findViewById(R.id.dateLbl);
        Date date = object.getCreatedAt();
        String dateFormat = new SimpleDateFormat("h:mm MMM d").format(date);
        timestampView.setText(type + " on " + dateFormat);

        return v;
    }
}
