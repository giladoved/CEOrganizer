package ceorganizer.oved.gilad.ceorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gilad on 6/4/15.
 */
public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_TYPE = "type";
    public final static String EXTRA_NUMBER = "number";
    public final static String EXTRA_ID = "parseobjectid";
    public final static String EXTRA_CALLER = "caller";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        final ListView listView = (ListView)findViewById(R.id.listView);

        /*MyArrayAdapter mainAdapter = new MyArrayAdapter(getApplicationContext());
        mainAdapter.setTextKey("summary");*/

        final List<ParseObject> threads = new ArrayList<>();
        final ArrayList<String> names = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Summary");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject obj : list) {
                        String name = obj.getString("caller");
                        if (!names.contains(name)) {
                            names.add(name);
                            threads.add(obj);
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_list_item_1, android.R.id.text1, names);
                    listView.setAdapter(adapter);
                } else {

                }
            }
        });

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                String caller = (String)listView.getItemAtPosition(pos);
                Intent i = new Intent(MainActivity.this, EventDetails.class);
                i.putExtra(EXTRA_CALLER, caller);
                startActivity(i);
            }
        });

        Intent intent = new Intent(this, CallService.class);
        startService(intent);
    }


}