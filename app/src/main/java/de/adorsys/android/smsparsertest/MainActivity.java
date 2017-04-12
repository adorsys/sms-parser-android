package de.adorsys.android.smsparsertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import de.adorsys.android.smsparser.SmsConfig;
import de.adorsys.android.smsparser.SmsReceiver;
import de.adorsys.android.smsparser.SmsTool;

/**
 * @author Drilon Reçica
 */

public class MainActivity extends AppCompatActivity {
    @NonNull
    private TextView smsSenderTextView;
    @NonNull
    private TextView smsMessageTextView;
    @NonNull
    private LocalBroadcastManager localBroadcastManager;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SmsReceiver.INTENT_ACTION_SMS)) {
                String receivedSender = intent.getStringExtra(SmsReceiver.KEY_SMS_SENDER);
                String receivedMessage = intent.getStringExtra(SmsReceiver.KEY_SMS_MESSAGE);
                smsSenderTextView.setText(getString(R.string.text_sms_sender_number,
                        receivedSender != null ? receivedSender : "NO NUMBER"));
                smsMessageTextView.setText(getString(R.string.text_sms_message,
                        receivedMessage != null ? receivedMessage : "NO MESSAGE"));
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case SmsTool.REQUEST_CODE_ASK_PERMISSIONS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.warning_permission_not_granted),
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + getApplicationContext().getPackageName())));
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SmsConfig.INSTANCE.initializeSmsConfig(
                "BEGIN-MESSAGE",
                "END-MESSAGE",
                "0900123456", "0900654321", "0900900900");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SmsTool.requestSMSPermission(this);
        }

        initViews();
    }

    @Override
    protected void onPause() {
        unRegisterReceiver();
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver();
        super.onResume();
    }

    private void initViews() {
        smsSenderTextView = (TextView) findViewById(R.id.sms_sender_text_view);
        smsMessageTextView = (TextView) findViewById(R.id.sms_message_text_view);

        smsSenderTextView.setText(getString(R.string.text_sms_sender_number, ""));
        smsMessageTextView.setText(getString(R.string.text_sms_message, ""));
    }

    private void registerReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsReceiver.INTENT_ACTION_SMS);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void unRegisterReceiver() {
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}