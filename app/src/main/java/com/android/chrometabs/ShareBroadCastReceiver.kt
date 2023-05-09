package com.android.chrometabs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ShareBroadCastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val url = intent?.dataString
        Log.d("AKSHAT", "onReceive: $url")
    }
}