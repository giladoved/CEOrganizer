package ceorganizer.oved.gilad.ceorganizer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by gilad on 6/10/15.
 */
public class CallService extends Service {
    private CallListener listener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        listener = new CallListener(this);
        listener.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listener.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
