package de.adorsys.android.smsparser

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val senderList = SmsConfig.smsSenderNumbers
        if (INTENT_ACTION_SMS_RECEIVED == intent.action) {
            val bundle = intent.extras
            val smsMessages: MutableList<SmsMessage?>
            var messageFrom: String? = null
            if (bundle != null) {
                //PDU = protocol data unit
                //A PDU is a “protocol data unit”, which is the industry format for an SMS message.
                //Because SMSMessage reads/writes them you shouldn't need to dissect them.
                //A large message might be broken into many, which is why it is an array of objects.
                val pdus = bundle.get("pdus") as Array<*>?
                if (pdus != null) {
                    // If the sent message is longer than 160 characters  it will be broken down
                    // in to chunks of 153 characters before being received on the device.
                    // To rectify that receivedMessage is the result of appending every single
                    // short message into one large one for our usage. see:
                    //http://www.textanywhere.net/faq/is-there-a-maximum-sms-message-length\
                    smsMessages = arrayOfNulls<SmsMessage>(pdus.size).toMutableList()
                    for (i in smsMessages.indices) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            smsMessages.add(i, SmsMessage.createFromPdu(pdus[i] as ByteArray, bundle.getString("format")))
                        } else {
                            @Suppress("DEPRECATION")
                            smsMessages.add(i, SmsMessage.createFromPdu(pdus[i] as ByteArray))
                        }
                        messageFrom = smsMessages[i]?.originatingAddress
                    }
                    if (!messageFrom.isNullOrBlank() && senderList.contains(messageFrom)) {
                        val receivedMessage = StringBuilder()
                        for (smsMessage in smsMessages) {
                            receivedMessage.append(smsMessage?.messageBody)
                        }
                        sendBroadcast(context, messageFrom, receivedMessage.toString())
                    } else {
                        sendBroadcast(context, null, null)
                    }
                }
            }
        }
    }

    private fun sendBroadcast(context: Context,
                              messageFrom: String?,
                              smsMessage: String?) {
        val broadcastIntent = Intent(INTENT_ACTION_SMS)

        var smsCode: String? = null
        if (smsMessage != null) {
            try {
                smsCode = getSmsCode(smsMessage)
            } catch (e: StringIndexOutOfBoundsException) {
                if (BuildConfig.DEBUG) {
                    val loggingMessage: String = if (e.message == null) {
                        "StringIndexOutOfBoundsException while retrieving SmsCode"
                    } else {
                        e.message!!
                    }
                    Log.d(SmsReceiver::class.java.name, loggingMessage)
                }
            }
        }

        broadcastIntent.putExtra(KEY_SMS_SENDER, messageFrom)
        broadcastIntent.putExtra(KEY_SMS_MESSAGE, smsCode)
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent)
    }

    private fun getSmsCode(message: String): String {
        val beginIndexSingleton = SmsConfig.beginIndex
        val endIndexSingleton = SmsConfig.endIndex

        val startIndex = message.indexOf(beginIndexSingleton)
        val endIndex = message.indexOf(endIndexSingleton)

        return message.substring(startIndex, endIndex)
                .replace(beginIndexSingleton, "")
                .trim { it <= ' ' }
    }

    companion object {
        const val INTENT_ACTION_SMS = "intent_action_sms"
        const val KEY_SMS_SENDER = "key_sms_sender"
        const val KEY_SMS_MESSAGE = "key_sms_message"

        private const val INTENT_ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
    }
}