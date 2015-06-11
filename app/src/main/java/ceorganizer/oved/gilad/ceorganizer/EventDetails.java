package ceorganizer.oved.gilad.ceorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.parse.ParseQueryAdapter;


public class EventDetails extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        final ListView listView = (ListView)findViewById(R.id.eventListView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String caller = getIntent().getStringExtra(MainActivity.EXTRA_CALLER);
        ParseQueryAdapter adapter = new MyArrayAdapter(EventDetails.this, caller);
        listView.setAdapter(adapter);
        adapter.loadObjects();

        setTitle(caller);
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
