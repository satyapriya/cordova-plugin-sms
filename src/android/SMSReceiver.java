package com.rjfun.cordova.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import org.json.JSONObject;

public class SMSReceiver extends BroadcastReceiver
{
    private static String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i(TAG, "in onReceive");

        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
            Log.i(TAG, "Reading Bundle");

            SmsMessage smsMessage;
            if (Build.VERSION.SDK_INT >= 19)
            { //KITKAT
                Log.i(TAG, "Version >= 19");
                SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                smsMessage = msgs[0];
            } else
            {
                Log.i(TAG, "Version < 19");
                Object pdus[] = (Object[]) bundle.get("pdus");
                smsMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
            }

            JSONObject json = getJsonFromSmsMessage(smsMessage);

            SMSPlugin.onSMSArrive(json);

            if (smsMessage.getMessageBody().toLowerCase().contains("test123"))
            {
                startActivity(json);
            }
        }

        Log.i(TAG, "out onReceive");
    }

    /**
     * Open app when received FCM message.
     *
     * @param data FCM data.
     */
    private void startActivity(JSONObject data)
    {
        Intent intent = new Intent(this, FCMPluginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("navCoords", data);
        this.startActivity(intent);
    }


    private JSONObject getJsonFromSmsMessage(SmsMessage sms) {
    	JSONObject json = new JSONObject();
    	
        try {
        	json.put( ADDRESS, sms.getOriginatingAddress() );
        	json.put( BODY, sms.getMessageBody() ); // May need sms.getMessageBody.toString()
        	json.put( DATE_SENT, sms.getTimestampMillis() );
        	json.put( DATE, System.currentTimeMillis() );
        	json.put( READ, MESSAGE_IS_NOT_READ );
        	json.put( SEEN, MESSAGE_IS_NOT_SEEN );
        	json.put( STATUS, sms.getStatus() );
        	json.put( TYPE, MESSAGE_TYPE_INBOX );
        	json.put( SERVICE_CENTER, sms.getServiceCenterAddress());
        	
        } catch ( Exception e ) { 
            e.printStackTrace(); 
        }

    	return json;
    }
}