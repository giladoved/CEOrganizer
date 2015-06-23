package ceorganizer.oved.gilad.ceorganizer;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gilad on 6/10/15.
 */
public class CallService extends Service {
    private CallListener listener;

    WindowManager wm;
    LinearLayout llayout;
    Handler handler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        listener = new CallListener(this);
        listener.start();
        handler = new Handler();

        final ActivityManager am =  (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        Timer timer  =  new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (am.getRunningTasks(1).get(0).topActivity.getClassName().equals("com.android.contacts.activities.DialtactsActivity")) {
                    handler.post(new Runnable() { // This thread runs in the UI
                        @Override
                        public void run() {
                            if (llayout == null)
                                addSubjectLine();
                        }
                    });
                }
            }
        }, 0, 5000);  // every 3 seconds


        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
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

    public void addSubjectLine() {
        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params1 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSPARENT);
        Display display = wm.getDefaultDisplay();
        params1.height = 150;
        params1.width = display.getWidth();
        params1.x = 0;
        params1.y = -450;
        params1.format = PixelFormat.TRANSLUCENT;

        llayout = new LinearLayout(getApplicationContext());
        llayout.setBackgroundColor(Color.GRAY);
        llayout.setOrientation(LinearLayout.HORIZONTAL);
        llayout.setWeightSum(1);

        Button cancelBtn = new Button(getApplicationContext());
        cancelBtn.setText("x");
        cancelBtn.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, .45f));
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSubjectLine();
            }
        });
        llayout.addView(cancelBtn);

        final EditText textEdit = new EditText(getApplicationContext());
        textEdit.setHint("Subject Line");
        textEdit.setLines(1);
        textEdit.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, .1f));
        llayout.addView(textEdit);

        Button btn = new Button(getApplicationContext());
        btn.setText("✓");
        btn.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, .45f));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject request = new ParseObject("Request");
                request.put("from", ParseUser.getCurrentUser());
                request.put("subject", textEdit.getText().toString().trim());
                request.saveInBackground();

                removeSubjectLine();
            }
        });
        llayout.addView(btn);

        wm.addView(llayout, params1);
    }

    public void removeSubjectLine() {
        if (llayout != null)
            wm.removeView(llayout);
    }
}
