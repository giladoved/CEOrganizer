package ceorganizer.oved.gilad.ceorganizer;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by gilad on 6/4/15.
 */
public class MainApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "mpHxKZNNkVjBDpfiOeJyMkKfC2EWgJcqOyMG0f8b", "c7VnUH6kocoQnxkXAhY4SA682jqAPODXqj1kEIUo");
    }

}
