package ceorganizer.oved.gilad.ceorganizer;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseUser;

/**
 * Created by gilad on 6/4/15.
 */
public class MainApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "mpHxKZNNkVjBDpfiOeJyMkKfC2EWgJcqOyMG0f8b", "c7VnUH6kocoQnxkXAhY4SA682jqAPODXqj1kEIUo");
        ParseUser.enableRevocableSessionInBackground();

        //ParseACL defaultACL = new ParseACL();
        //ParseACL.setDefaultACL(defaultACL, true);
    }

}
