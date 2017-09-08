# SMS Parser - Android

## Receiving and Parsing SMS Messages on Android Devices

[![Build Status](https://travis-ci.org/adorsys/sms-parser-android.svg?branch=master)](https://travis-ci.org/adorsys/sms-parser-android)
[![Download](https://api.bintray.com/packages/andev/adorsys/smsparser/images/download.svg)](https://bintray.com/andev/adorsys/smsparser/_latestVersion)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-SMS%20Parser%20Android-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5652)
[![API](https://img.shields.io/badge/API-14%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=14)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) 
[![Open Source Love](https://badges.frapsoft.com/os/v1/open-source.svg?v=103)](https://github.com/ellerbrock/open-source-badges/)

### Introduction

This module was created for getting specific codes out of incoming SMS messages.
To use it you create a Config object where you specify the ___BEGIN___ and ___END___ message phrases & the ___SMS sender numbers___ from which you want to read the sms content.
The received code then can be used according to your need (activation, authentication etc...).

### How it Works

If you get a SMS message like this:

```
This is an automated message used by X-App. You can delete this message at any time.
BEGIN-MESSAGE
08ff08d3b2981eb6c611a385ffa4f865
END-MESSAGE
```
and the sender of the message is one of the numbers you have specified in your Config object the parsing process will begin.

First the module reads the whole message.
It then checks if it contains the ___BEGIN___ and ___END___ phrases you specified.
If it does then it takes only the part that is between those phrases and send a broadcast to you app with the specific code and the sender phone number.
In this case the module would send a broadcast containing `08ff08d3b2981eb6c611a385ffa4f865` and the sender number.

For more info read ___Usage___.

### Usage

Add the module to your apps build.gradle:

```golang
compile 'de.adorsys.android:smsparser:0.0.3'
```

First of all you have to create a Config object to configure the modules use:
```java
        SmsConfig.INSTANCE.initializeSmsConfig(
                "BEGIN-MESSAGE",
                "END-MESSAGE",
                "0900123456", "0900654321", "0900900900");
```
Here the two parameters are the keywords that will be used for the code to be extracted from the sms message.
The ___third___ parameter is a varargs (...) parameter, where you can give a series of phone numbers (as Strings), which will be checked against for reading sms content.

Then before startinganything you have to aks for the SMS Permmision on Android 6.0 and higher, or else the module won't work:
```java
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SmsTool.requestSMSPermission(Context);
        }
```

Then you need to create a LocalBroadcastManager and BroadcastReceiver, like this:
```java
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
```

LocalBroadcastManager is used instead of standard BroadcastManager for security reasons, so no other app can listen to the broadcasts that are sent.

You must create register and unregister methods for the BroadcastReceiver and call them in onResume() and onPause(), respectively:
```java
    @Override
    protected void onResume() {
        registerReceiver();
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        unRegisterReceiver();
        super.onPause();
    }

    private void registerReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(Context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsReceiver.INTENT_ACTION_SMS);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void unRegisterReceiver() {
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
```



### Contributors:
[@drilonreqica](https://github.com/drilonreqica)

[@itsmortoncornelius](https://github.com/itsmortoncornelius)
