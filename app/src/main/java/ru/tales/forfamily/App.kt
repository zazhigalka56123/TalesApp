package ru.tales.forfamily

import android.app.Application
import android.util.Log
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import ru.tales.forfamily.domain.SP
import java.time.temporal.IsoFields


class App: Application() {
    override fun onCreate() {
        super.onCreate()
        val LOG_TAG = "APP_LOGG"
        storage = SP(this)
        val appsflyer = AppsFlyerLib.getInstance()

        appsflyer.setDebugLog(true);
        appsflyer.init("fqqn3Q6AtbBcuvfoX49kxi", null, this)
        appsflyer.start(this, "fqqn3Q6AtbBcuvfoX49kxi", object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                print( "Launch sent successfully")
            }

            override fun onError(errorCode: Int, errorDesc: String) {
                println( "Launch failed to be sent: " +
                        "Error code: " + errorCode + " "
                        + "Error description: " + errorDesc)
            }
        })
        appsflyer.subscribeForDeepLink(DeepLinkListener { deepLinkResult ->
            when (deepLinkResult.status) {
                DeepLinkResult.Status.FOUND -> {
                    Log.d(LOG_TAG, "Deep link found")
                }
                DeepLinkResult.Status.NOT_FOUND -> {
                    Log.d(LOG_TAG, "Deep link not found")
                    return@DeepLinkListener
                }
                else -> {
                    val dlError = deepLinkResult.error
                    Log.d(
                        LOG_TAG, "There was an error getting Deep Link data: $dlError")
                    return@DeepLinkListener
                }
            }

            val deepLinkObj = deepLinkResult.deepLink
            try {
                Log.d(LOG_TAG, "The DeepLink data is: $deepLinkObj")
            } catch (e: Exception) {
                Log.d(LOG_TAG, "DeepLink data came back null")
                return@DeepLinkListener
            }
            try {
                val data = deepLinkObj.deepLinkValue
                if (data == "addCount") {
                    storage.showPopUp = true
                }
                if (data == "trackingUserId"){
                    val userId = deepLinkObj.getStringValue("userId")
                    val send = deepLinkObj.getStringValue("send")
                    if (!userId.isNullOrEmpty() && (send.toString() == "1" || send.toString() == "0")){
                        storage.isTackingUserId = true
                        storage.trackingTypeUserId = send.toString().toInt()
                        storage.userId = userId.toString()
                        Log.d(LOG_TAG, "success ${storage.isTackingUserId} ${storage.trackingTypeUserId} ${storage.userId} end")

                    }

                    Log.d("data", "data $data $userId $send")

                }

                Log.d(LOG_TAG, "The DeepLink data: $data")
            }catch (e: Exception) {
                Log.d(LOG_TAG, "Custom param fruit_name was not found in DeepLink data")
                return@DeepLinkListener
            }
        })

    }

    companion object {
        lateinit var storage: SP
    }
}