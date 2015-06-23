package ceorganizer.oved.gilad.ceorganizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

/**
 * Created by gilad on 6/10/15.
 */
public class CallListener  {
    String incomingNum;
    LinearLayout ly1;
    LinearLayout ly2;
    WindowManager wm;

    /**
     * Listener to detect incoming calls.
     */
    private class CallStateListener extends PhoneStateListener {
        int lastState = TelephonyManager.CALL_STATE_IDLE;
        Date callStartTime;
        boolean isIncoming;
        boolean shouldShowProfile;

        //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
        //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            if (lastState == state) {
                return;
            }

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    isIncoming = true;
                    callStartTime = new Date();
                    Toast.makeText(context, "incoming call started from " + incomingNumber, Toast.LENGTH_SHORT).show();
                    addProfilePopup();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //Transition of ringing->offhook are pickups of incoming calls.  Nothing donw on them
                    if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                        isIncoming = false;
                        callStartTime = new Date();
                        Toast.makeText(context, "outgoing call started from " + incomingNumber, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                    if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                        //Ring but no pickup-  a miss
                        ParseObject summaryObj = new ParseObject("Summary");
                        summaryObj.put("type", "missed call");
                        summaryObj.put("summary", "");
                        summaryObj.put("caller", incomingNum);
                        summaryObj.saveInBackground();
                        //Toast.makeText(context, "missed call from " + incomingNumber, Toast.LENGTH_SHORT).show();
                    } else if (isIncoming) {
                        //Toast.makeText(context, "incoming call ended from " + incomingNumber, Toast.LENGTH_SHORT).show();
                        removeProfilePopup();

                        Intent i = new Intent(context, AddSummary.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra(MainActivity.EXTRA_TYPE, "incoming call");
                        i.putExtra(MainActivity.EXTRA_NUMBER, incomingNum);
                        context.startActivity(i);
                    } else {
                        //Toast.makeText(context, "outgoing call ended from " + outgoingNumber, Toast.LENGTH_SHORT).show();
                        removeProfilePopup();

                        Intent i = new Intent(context, AddSummary.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra(MainActivity.EXTRA_TYPE, "outgoing call");
                        i.putExtra(MainActivity.EXTRA_NUMBER, outgoingNumber);
                        context.startActivity(i);
                    }
                    break;
            }
            lastState = state;
        }

        //http://stackoverflow.com/questions/23701879/popup-window-on-incoming-call-screen-like-truecaller
        public void addProfilePopup() {
            wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams params1 = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT);
            Display display = wm.getDefaultDisplay();
            params1.height = display.getWidth()/4;
            params1.width = display.getWidth();
            params1.x = 0;
            params1.y = -100;
            params1.format = PixelFormat.TRANSLUCENT;

            ly1 = new LinearLayout(context);
            ly1.setBackgroundColor(Color.GRAY);
            ly1.setOrientation(LinearLayout.VERTICAL);

            final TextView nameText = new TextView(context);
            ly1.addView(nameText);

            final TextView jobText = new TextView(context);
            ly1.addView(jobText);

            final TextView companyText = new TextView(context);
            ly1.addView(companyText);

            final TextView numberText = new TextView(context);
            ly1.addView(numberText);

            wm.addView(ly1, params1);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    ParseObject request = parseObjects.get(0);
                    String name = request.getString("name");
                    String job = request.getString("job");
                    String company = request.getString("company");
                    String number = request.getString("number");

                    nameText.setText(name);
                    jobText.setText(job);
                    companyText.setText(company);
                    numberText.setText(number);
                }
            });
        }

        public void removeProfilePopup() {
            if (ly1 != null)
                wm.removeView(ly1);
        }

    }

    /**
     * Broadcast receiver to detect the outgoing calls.
     */
    public class OutgoingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Toast.makeText(context, "NUM!!-: " + number, Toast.LENGTH_LONG).show();
            outgoingNumber = number;


            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                // Outgoing call
                String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Log.d("SWAG", "PhoneStateReceiver **Outgoing call " + outgoingNumber);

                //setResultData(null); // Kills the outgoing call
            }
        }

    }

    private Context context;
    private TelephonyManager teleManager;
    private CallStateListener stateListener;
    String outgoingNumber;

    private OutgoingReceiver outgoingReceiver;

    public CallListener(Context context) {
        this.context = context;

        stateListener = new CallStateListener();
        outgoingReceiver = new OutgoingReceiver();
    }

    public void start() {
        teleManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        teleManager.listen(stateListener, PhoneStateListener.LISTEN_CALL_STATE);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        context.registerReceiver(outgoingReceiver, intentFilter);
    }

    public void stop() {
        teleManager.listen(stateListener, PhoneStateListener.LISTEN_NONE);
        context.unregisterReceiver(outgoingReceiver);
    }
}
