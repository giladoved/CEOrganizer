package ceorganizer.oved.gilad.ceorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class AddSummary extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_summary);

        Intent intent = getIntent();
        final String type = intent.getStringExtra(MainActivity.EXTRA_TYPE);
        final String number = intent.getStringExtra(MainActivity.EXTRA_NUMBER);

        final EditText summaryText = (EditText)findViewById(R.id.summaryText);
        Button saveSummaryBtn = (Button) findViewById(R.id.saveSummaryBtn);
        saveSummaryBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ParseObject summaryObj = new ParseObject("Summary");
                summaryObj.put("summary", summaryText.getText().toString());
                summaryObj.put("type", type);
                summaryObj.put("caller", number);
                summaryObj.put("self", ParseUser.getCurrentUser());
                summaryObj.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        finish();
                        Intent i = new Intent(AddSummary.this, MainActivity.class);
                        startActivity(i);
                    }
                });
            }
        });
    }


}
