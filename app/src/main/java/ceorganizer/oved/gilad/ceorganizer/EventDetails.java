package ceorganizer.oved.gilad.ceorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class EventDetails extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        final ListView listView = (ListView)findViewById(R.id.eventListView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String caller = getIntent().getStringExtra(MainActivity.EXTRA_CALLER);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Summary");
        query.whereEqualTo("caller", caller);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    ArrayList<String> summaries = new ArrayList<String>();
                    for (ParseObject obj : list) {
                        summaries.add(obj.getString("summary"));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EventDetails.this,
                            android.R.layout.simple_list_item_1, android.R.id.text1, summaries);
                    listView.setAdapter(adapter);
                } else {

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
