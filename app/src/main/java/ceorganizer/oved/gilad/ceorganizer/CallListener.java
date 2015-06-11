package ceorganizer.oved.gilad.ceorganizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by gilad on 6/10/15.
 */
public class CallListener  {

    /**
     * Listener to detect incoming calls.
     */
    private class CallStateListener extends PhoneStateListener {
        int lastState = TelephonyManager.CALL_STATE_IDLE;
        Date callStartTime;
        boolean isIncoming;

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
                    //Toast.makeText(getApplicationContext(), "incoming call started from " + incomingNumber, Toast.LENGTH_SHORT).show();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //Transition of ringing->offhook are pickups of incoming calls.  Nothing donw on them
                    if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                        isIncoming = false;
                        callStartTime = new Date();
                        //Toast.makeText(getApplicationContext(), "outgoing call started from " + savedNumber, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                    if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                        //Ring but no pickup-  a miss
                        ParseObject summaryObj = new ParseObject("Summary");
                        summaryObj.put("type", "missed call");
                        summaryObj.put("summary", "");
                        summaryObj.put("caller", incomingNumber);
                        summaryObj.saveInBackground();
                        //Toast.makeText(context, "missed call from " + incomingNumber, Toast.LENGTH_SHORT).show();
                    } else if (isIncoming) {
                        //Toast.makeText(context, "incoming call ended from " + incomingNumber, Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(context, AddSummary.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra(MainActivity.EXTRA_TYPE, "incoming call");
                        i.putExtra(MainActivity.EXTRA_NUMBER, incomingNumber);
                        context.startActivity(i);
                    } else {
                        //Toast.makeText(context, "outgoing call ended from " + outgoingNumber, Toast.LENGTH_SHORT).show();

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
    }

    /**
     * Broadcast receiver to detect the outgoing calls.
     */
    public class OutgoingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            //Toast.makeText(context, "Outgoing---: "+number, Toast.LENGTH_LONG).show();
            outgoingNumber = number;
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
