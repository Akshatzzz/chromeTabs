package com.android.chrometabs

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Resources.Theme
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.*
import androidx.browser.customtabs.CustomTabsIntent.SHARE_STATE_OFF
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.core.graphics.drawable.toBitmap
import com.android.chrometabs.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var serviceConnection: CustomTabsServiceConnection
    lateinit var client: CustomTabsClient
    lateinit var session: CustomTabsSession
    var builder = CustomTabsIntent.Builder()
    var url = "https://www.mi.com/in"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
        CustomTabsClient.bindCustomTabsService(
            this@MainActivity, "com.android.chrome", serviceConnection
        )

    }

    private fun showDataFromIntent() {
        val data: Uri? = intent?.data

        // Figure out what to do based on the intent type
        if (intent?.type?.startsWith("image/") == true) {
            // Handle intents with image data ...
            Log.d("Intent", intent?.type.toString())
        } else if (intent?.type == "text/plain") {
            // Handle intents with text ...
            Log.d("Intent.text", intent.extras?.getString("android.intent.extra.TEXT").toString())
        } else {
            if (data != null) {
                Log.d("Intent URL", data.toString())
            }
        }
    }

    private fun initialize() {
        val height = getHeightInPercentage(80, this@MainActivity)
        serviceConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                name: ComponentName, mClient: CustomTabsClient
            ) {
                val sendIntent = Intent(this@MainActivity, ShareBroadCastReceiver::class.java)
                val requestCode = 0 // request code used to identify the PendingIntent
                val pendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    requestCode,
                    sendIntent,
                    PendingIntent.FLAG_MUTABLE
                )
                AppCompatResources.getDrawable(this@MainActivity, R.drawable.baseline_share_24)
                    ?.toBitmap()
                    ?.let { builder.setActionButton(it, "ActionButton", pendingIntent, true) }
                Log.d("Service", "Connected")
                client = mClient
                client.warmup(0L)
                val callback = TabsCallBack()
                session = mClient.newSession(callback)!!
                builder.setSession(session)
                builder.setInitialActivityHeightPx(height)
                builder.setColorSchemeParams(
                    CustomTabsIntent.COLOR_SCHEME_LIGHT,
                    CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(resources.getColor(R.color.purple_500)).build()
                )
                builder.setColorSchemeParams(
                    CustomTabsIntent.COLOR_SCHEME_DARK,
                    CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(resources.getColor(R.color.teal_700)).build()
                )
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Log.d("Service", "Disconnected")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        CustomTabsClient.bindCustomTabsService(
            this@MainActivity, "com.android.chrome", serviceConnection
        )
    }

    override fun onResume() {
        super.onResume()
        // The URL is stored in the intent's data
        val data: Uri? = intent?.data

        // Figure out what to do based on the intent type
        if (intent?.type?.startsWith("image/") == true) {
            // Handle intents with image data ...
            Log.d("Intent", intent?.type.toString())
        } else if (intent?.type == "text/plain") {
            // Handle intents with text ...
            Log.d("Intent.text", intent.extras?.getString("android.intent.extra.TEXT").toString())
        } else {
            if (data != null) {
                Log.d("Intent URL", data.toString())
            }
        }
        binding.launchButton.setOnClickListener {
            url = getUrlToBeLaunched(url)
            val customTabsIntent: CustomTabsIntent = builder.build()
            customTabsIntent.launchUrl(this@MainActivity, Uri.parse(url))
        }
    }

    private fun getUrlToBeLaunched(url: String): String {
        if (binding.etUrl.text.isNullOrEmpty()) {
            return url
        }
        return binding.etUrl.text.toString()
    }

    fun getHeightInPercentage(percentage: Int, context: Context): Int {
        val height = getScreenHeight(context)
        val factor = percentage.toFloat() / 100f
        return (height * factor).toInt()
    }

    fun getScreenHeight(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        return dm.heightPixels
    }


    class TabsCallBack : CustomTabsCallback() {
        override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
            super.onNavigationEvent(navigationEvent, extras)
            Log.d("Nav", navigationEvent.toString())
            when (navigationEvent) {
                NAVIGATION_STARTED -> Log.d("Navigation", "Start") // NAVIGATION_STARTED
                NAVIGATION_FINISHED -> Log.d("Navigation", "Finished") // NAVIGATION_FINISHED
                NAVIGATION_FAILED -> Log.d("Navigation", "Failed") // NAVIGATION_FAILED
                NAVIGATION_ABORTED -> Log.d("Navigation", "Aborted") // NAVIGATION_ABORTED
                TAB_SHOWN -> Log.d("Navigation", "Tab Shown") // TAB_SHOWN
                TAB_HIDDEN -> Log.d("Navigation", "Tab Hidden") // TAB_HIDDEN
                else -> Log.d("Navigation", "Else")
            }
        }
    }
}