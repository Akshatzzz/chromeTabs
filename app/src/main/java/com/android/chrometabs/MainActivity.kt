package com.android.chrometabs

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.*
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
        serviceConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                name: ComponentName, mClient: CustomTabsClient
            ) {
                Log.d("Service", "Connected")
                client = mClient
                client.warmup(0L)
                val callback = TabsCallBack()
                session = mClient.newSession(callback)!!
                builder.setSession(session)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Log.d("Service", "Disconnected")
            }
        }
        CustomTabsClient.bindCustomTabsService(this@MainActivity, "com.android.chrome", serviceConnection)
    }

    override fun onStart() {
        super.onStart()
        CustomTabsClient.bindCustomTabsService(this@MainActivity, "com.android.chrome", serviceConnection)
    }

    override fun onResume() {
        super.onResume()
        val height = getHeightInPercentage(80,this@MainActivity)
        binding.launchButton.setOnClickListener {
            url = getUrlToBeLaunched(url)
            builder.setInitialActivityHeightPx(height)
            val customTabsIntent: CustomTabsIntent = builder.build()
            customTabsIntent.launchUrl(this@MainActivity, Uri.parse(url))
        }
    }

    private fun getUrlToBeLaunched(url: String): String {
        if(binding.etUrl.text.isNullOrEmpty()) {
            return url
        }
        return binding.etUrl.text.toString()
    }

    fun getHeightInPercentage(percentage : Int,context: Context): Int {
        val height = getScreenHeight(context)
        val factor = percentage.toFloat()/100f
        return (height*factor).toInt()
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