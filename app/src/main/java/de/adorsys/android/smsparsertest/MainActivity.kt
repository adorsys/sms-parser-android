package de.adorsys.android.smsparsertest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import de.adorsys.android.smsparser.SmsConfig
import de.adorsys.android.smsparser.SmsReceiver
import de.adorsys.android.smsparser.SmsTool

class MainActivity : AppCompatActivity() {
    private lateinit var smsSenderTextView: TextView
    private lateinit var smsMessageTextView: TextView
    private lateinit var localBroadcastManager: LocalBroadcastManager

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsReceiver.INTENT_ACTION_SMS == intent.action) {
                val receivedSender = intent.getStringExtra(SmsReceiver.KEY_SMS_SENDER)
                val receivedMessage = intent.getStringExtra(SmsReceiver.KEY_SMS_MESSAGE)
                smsSenderTextView.text = getString(R.string.text_sms_sender_number,
                        receivedSender ?: "NO NUMBER")
                smsMessageTextView.text = getString(R.string.text_sms_message,
                        receivedMessage ?: "NO MESSAGE")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == SmsTool.REQUEST_CODE_ASK_PERMISSIONS && (grantResults.size <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, getString(R.string.warning_permission_not_granted),
                    Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + applicationContext.packageName)))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SmsConfig.initializeSmsConfig(
                "BEGIN-MESSAGE",
                "END-MESSAGE",
                "0900123456", "0900654321", "0900900900")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SmsTool.requestSMSPermission(this)
        }

        initViews()
    }

    override fun onPause() {
        unRegisterReceiver()
        super.onPause()
    }

    override fun onResume() {
        registerReceiver()
        super.onResume()
    }

    private fun initViews() {
        smsSenderTextView = findViewById(R.id.sms_sender_text_view)
        smsMessageTextView = findViewById(R.id.sms_message_text_view)

        smsSenderTextView.text = getString(R.string.text_sms_sender_number, "")
        smsMessageTextView.text = getString(R.string.text_sms_message, "")
    }

    private fun registerReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsReceiver.INTENT_ACTION_SMS)
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun unRegisterReceiver() {
        localBroadcastManager.unregisterReceiver(broadcastReceiver)
    }
}