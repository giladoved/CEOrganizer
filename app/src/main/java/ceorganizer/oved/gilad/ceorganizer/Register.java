package ceorganizer.oved.gilad.ceorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class Register extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (ParseUser.getCurrentUser() != null) {
            Intent i = new Intent(Register.this, MainActivity.class);
            startActivity(i);
        }

        final EditText nameTxt = (EditText)findViewById(R.id.nameText);
        final EditText companyTxt = (EditText)findViewById(R.id.companyText);
        final EditText positionTxt = (EditText)findViewById(R.id.positionText);
        final EditText numberTxt = (EditText)findViewById(R.id.phoneNumberText);
        numberTxt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        Button registerButton = (Button)findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String name = nameTxt.getText().toString().trim();
                String company = companyTxt.getText().toString().trim();
                String position = positionTxt.getText().toString().trim();
                String number = numberTxt.getText().toString().trim();
                ParseUser user = new ParseUser();
                user.setUsername(name);
                user.setPassword(name);
                user.put("company", company);
                user.put("position", position);
                user.put("number", number);
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Intent i = new Intent(Register.this, MainActivity.class);
                            startActivity(i);
                            // Show a simple Toast message upon successful registration
                            Toast.makeText(getApplicationContext(),
                                    "Successfully signed up... Enjoy!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Sign up Error", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
            }
        });
    }
}
