/*
 * Copyright (C) 2017 adorsys GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.adorsys.android.smsparser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.BuildConfig;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class SmsReceiver extends BroadcastReceiver {
    public static final String INTENT_ACTION_SMS = "intent_action_sms";
    public static final String KEY_SMS_SENDER = "key_sms_sender";
    public static final String KEY_SMS_MESSAGE = "key_sms_message";

    private static final String INTENT_ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String[] senderArray = SmsConfig.INSTANCE.getSmsSenderNumbers();
        if (intent.getAction().equals(INTENT_ACTION_SMS_RECEIVED) && senderArray != null) {
            List<String> smsSenderNumbers = Arrays.asList(senderArray);
            Bundle bundle = intent.getExtras();
            SmsMessage[] smsMessages;
            String messageFrom = null;
            if (bundle != null) {
                //PDU = protocol data unit
                //A PDU is a “protocol data unit”, which is the industry format for an SMS message.
                //Because SMSMessage reads/writes them you shouldn't need to dissect them.
                //A large message might be broken into many, which is why it is an array of objects.
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    smsMessages = new SmsMessage[pdus.length];
                    // If the sent message is longer than 160 characters  it will be broken down
                    // in to chunks of 153 characters before being received on the device.
                    // To rectify that receivedMessage is the result of appending every single
                    // short message into one large one for our usage. see:
                    //http://www.textanywhere.net/faq/is-there-a-maximum-sms-message-length

                    for (int i = 0; i < smsMessages.length; i++) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i],
                                    bundle.getString("format"));
                        } else {
                            smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        }
                        messageFrom = smsMessages[i].getOriginatingAddress();
                    }
                    if (!TextUtils.isEmpty(messageFrom)
                            && smsSenderNumbers.contains(messageFrom)) {
                        String receivedMessage = "";
                        for (SmsMessage smsMessage : smsMessages) {
                            receivedMessage = receivedMessage + smsMessage.getMessageBody();
                        }
                        sendBroadcast(context, messageFrom, receivedMessage);
                    } else {
                        sendBroadcast(context, null, null);
                    }
                }
            }
        }
    }

    private void sendBroadcast(@NonNull Context context,
                               @Nullable String messageFrom,
                               @Nullable String smsMessage) {
        Intent broadcastIntent = new Intent(INTENT_ACTION_SMS);

        String smsCode = null;
        if (smsMessage != null) {
            try {
                smsCode = getSmsCode(smsMessage);
            } catch (StringIndexOutOfBoundsException e) {
                if (BuildConfig.DEBUG) {
                    Log.d(SmsReceiver.class.getName(), e.getMessage());
                }
            }
        }

        broadcastIntent.putExtra(KEY_SMS_SENDER, messageFrom);
        broadcastIntent.putExtra(KEY_SMS_MESSAGE, smsCode);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
    }

    @Nullable
    private String getSmsCode(@NonNull String message) {
        String beginIndexSingleton = SmsConfig.INSTANCE.getBeginIndex();
        String endIndexSingleton = SmsConfig.INSTANCE.getEndIndex();

        if (beginIndexSingleton != null && endIndexSingleton != null) {
            int startIndex = message.indexOf(beginIndexSingleton);
            int endIndex = message.indexOf(endIndexSingleton);

            return message.substring(startIndex, endIndex).replace(beginIndexSingleton, "").trim();
        } else {
            return message;
        }
    }
}