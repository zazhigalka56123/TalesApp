package ru.tales.forfamily.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import ru.tales.forfamily.App
import ru.tales.forfamily.R
import ru.tales.forfamily.data.remote.ApiRepositoryImpl
import ru.tales.forfamily.domain.player.PlayerService
import com.yandex.mobile.ads.appopenad.AppOpenAd
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {

    private var appOpenAd: AppOpenAd? = null
    private var interstitialAdLoader: InterstitialAdLoader? = null
    private var interstitialAd: InterstitialAd? = null
    private var isAdShowedOnColdStart = false
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(Intent(this, PlayerService::class.java))
        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        viewModel.onUserAlertCallback = {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val count = ApiRepositoryImpl().getSettings()
            if(count != 20 && count != App.storage.countAdd){
                Log.d("TAGGGGAA", "count: $count")
                Log.d("TAGGGGAA", "countPopUpShowed: ${App.storage.countPopUpShowed}")
                Log.d("TAGGGGAA", "countAdd: ${App.storage.countAdd}")
                App.storage.addShowed -= (App.storage.countPopUpShowed - 1) * App.storage.countAdd
                App.storage.addShowed = maxOf(App.storage.addShowed, 0)
                App.storage.countAdd = count
                App.storage.countPopUpShowed = 1
                Log.d("TAGGGGAA", "count: $count")
                Log.d("TAGGGGAA", "countPopUpShowed: ${App.storage.countPopUpShowed}")
                Log.d("TAGGGGAA", "countAdd: ${App.storage.countAdd}")
            }
        }



        MobileAds.initialize(this) {
            println(">>> MobileAds initialize")

            loadAppOpenAd()
            val processLifecycleObserver = DefaultProcessLifecycleObserver(
                onProcessCameForeground = ::showAppOpenAd
            )
            ProcessLifecycleOwner.get().lifecycle.addObserver(processLifecycleObserver)

            interstitialAdLoader = InterstitialAdLoader(this).apply {
                setAdLoadListener(object : InterstitialAdLoadListener {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        println(">>> ad loaded")
                        interstitialAd = ad
                        // The ad was loaded successfully. Now you can show loaded ad.
                    }

                    override fun onAdFailedToLoad(adRequestError: AdRequestError) {
                        println(">>> ad failted to load: ${adRequestError.description}")
                        // Ad failed to load with AdRequestError.
                        // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
                    }
                })
            }
            loadInterstitialAd()
        }
    }

    private fun showPopUp() {
        if (viewModel.mode.value == Mode.DEFAULT_MODE) {
            if (PlayerService.exoPlayer?.isPlaying == true) {
                PlayerService.exoPlayer?.pause()
            }

            val builder = AlertDialog.Builder(this)

            val customLayout: View = layoutInflater.inflate(R.layout.fragment_pop_up, null)
            builder.setView(customLayout)
            val dialog = builder.create()

            val hash = md5()

            val textHash = customLayout.findViewById<MaterialTextView>(R.id.materialTextView3)
            val btnClose = customLayout.findViewById<AppCompatImageView>(R.id.close)

            textHash.setText(hash)

            btnClose.isClickable = false
            lifecycleScope.launch(Dispatchers.IO) {
                val isSuccessful = ApiRepositoryImpl().postData(hash)
                withContext(Dispatchers.Main) {
                    if (isSuccessful) {
                        Toast.makeText(this@MainActivity, "Успешно", Toast.LENGTH_SHORT)
                            .show()
                        btnClose.isClickable = true
//                        dialog.dismiss()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Произошла ошибка",
                            Toast.LENGTH_SHORT
                        ).show()
                        btnClose.isClickable = true
//                        dialog.dismiss()
                    }
                }


            }


            btnClose.setOnClickListener { dialog.dismiss() }



            dialog.show()
            if (dialog.isShowing){
                App.storage.countPopUpShowed += 1
            }
        }

    }

    private fun showPopUpOld() {
        if (viewModel.mode.value == Mode.DEFAULT_MODE) {
            if (PlayerService.exoPlayer?.isPlaying == true) {
                PlayerService.exoPlayer?.pause()
            }

            val builder = AlertDialog.Builder(this)

            val customLayout: View = layoutInflater.inflate(R.layout.fragment_pop_up_old, null)
            builder.setView(customLayout)
            val dialog = builder.create()

            val etLogin = customLayout.findViewById<EditText>(R.id.et_login)

            val btnSave = customLayout.findViewById<MaterialButton>(R.id.btnSave)
            val btnClose = customLayout.findViewById<AppCompatImageView>(R.id.close)

            btnSave.setOnClickListener {
                val t = etLogin.text.toString()

                if (t.isNotEmpty()) {
                    it.isClickable = false
                    btnClose.isClickable = false
                    lifecycleScope.launch(Dispatchers.IO) {
                        val isSuccessful = ApiRepositoryImpl().postData(t)
                        withContext(Dispatchers.Main) {
                            if (isSuccessful) {
                                Toast.makeText(this@MainActivity, "Успешно", Toast.LENGTH_SHORT)
                                    .show()
                                it.isClickable = true
                                btnClose.isClickable = true
                                dialog.dismiss()
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Произошла ошибка",
                                    Toast.LENGTH_SHORT
                                ).show()
                                it.isClickable = true
                                btnClose.isClickable = true
                                dialog.dismiss()
                            }
                        }


                    }
                }
            }

            btnClose.setOnClickListener { dialog.dismiss() }



            dialog.show()
            if (dialog.isShowing){
                App.storage.countPopUpShowed += 1
            }
        }

    }
    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
    fun md5(): String {
        val input = getRandomString(18)

        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
    private fun loadAppOpenAd(){
        val appOpenAdLoader = AppOpenAdLoader(application)
        val appOpenAdLoadListener = object : AppOpenAdLoadListener {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                // The ad was loaded successfully. Now you can show loaded ad.
                this@MainActivity.appOpenAd = appOpenAd
                if (!isAdShowedOnColdStart){
                    showAppOpenAd()
                    isAdShowedOnColdStart = true
                }

//                println(">>> onAdLoaded")
            }

            override fun onAdFailedToLoad(adRequestError: AdRequestError) {
                // Ad failed to load with AdRequestError.
                // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
//                println(">>> onAdFailedToLoad")
            }
        }
        appOpenAdLoader.setAdLoadListener(appOpenAdLoadListener)


//        val AD_UNIT_ID = "demo-appopenad-yandex"
        val AD_UNIT_ID = "R-M-7724871-1"
        val adRequestConfiguration = AdRequestConfiguration.Builder(AD_UNIT_ID).build()

        appOpenAdLoader.loadAd(adRequestConfiguration)
    }
    private fun showAppOpenAd(){
         val appOpenAdEventListener = AdEventListener()
        appOpenAd?.setAdEventListener(appOpenAdEventListener)
        appOpenAd?.show(this@MainActivity)
    }
    private inner class AdEventListener : AppOpenAdEventListener {
        override fun onAdShown() {
//            println(">>> onAdShown")
            AppsFlyerLib.getInstance().logEvent(
                applicationContext,
                AFInAppEventType.AD_VIEW , emptyMap(),
                object : AppsFlyerRequestListener {
                    override fun onSuccess() {
//                        println( "Event sent successfully")
                    }
                    override fun onError(errorCode: Int, errorDesc: String) {
//                        println(  "Event failed to be sent:" +
//                                "Error code: " + errorCode + ""
//                                + "Error description: " + errorDesc)
                    }
                })
            // Called when ad is shown.
        }

        override fun onAdFailedToShow(adError: AdError) {
            // Called when ad failed to show..
            clearAppOpenAd()
            loadAppOpenAd()
//            println(">>> onAdFailedToShow")
        }

        override fun onAdDismissed() {
            // Called when ad is dismissed.
            // Clean resources after dismiss and preload new ad.
            clearAppOpenAd()
            loadAppOpenAd()
//            println(">>> onAdFailedToShow")
        }

        override fun onAdClicked() {
            // Called when a click is recorded for an ad.
//            println(">>> onAdClicked")


            AppsFlyerLib.getInstance().logEvent(
                applicationContext,
                AFInAppEventType.AD_CLICK , emptyMap()
            )
        }

        override fun onAdImpression(impressionData: ImpressionData?) {
            // Called when an impression is recorded for an ad.
            // Get Impression Level Revenue Data in argument.
//            println(">>> onAdImpression ${impressionData?.rawData}")
        }
    }

    private fun clearAppOpenAd() {
        appOpenAd?.setAdEventListener(null)
        appOpenAd = null
    }

    fun loadInterstitialAd() {
        val adRequestConfiguration = AdRequestConfiguration.Builder("R-M-7724871-2").build()
//        val adRequestConfiguration = AdRequestConfiguration.Builder("demo-interstitial-yandex").build()
        interstitialAdLoader?.loadAd(adRequestConfiguration)
    }
    fun showAd() {
        interstitialAd?.apply {
            setAdEventListener(object : InterstitialAdEventListener {
                override fun onAdShown() {
                    // Called when ad is shown.
                    println( ">>> onAdShown")
                    App.storage.addShowed += 1

                    Log.d("add-counter", App.storage.addShowed.toString())

                    AppsFlyerLib.getInstance().logEvent(
                        applicationContext,
                        AFInAppEventType.AD_VIEW , emptyMap()
                    )

                }
                override fun onAdFailedToShow(adError: AdError) {
                    // Called when an InterstitialAd failed to show.
                    // Clean resources after Ad dismissed
                    println( ">>> onAdFailedToShow ${adError.description}" )

                    destroyInterstitialAd()

                }
                override fun onAdDismissed() {
                    // Called when ad is dismissed.
                    // Clean resources after Ad dismissed
                    println( ">>> onAdDismissed")
//                    destroyInterstitialAd()
                    println( ">>> try play")
                    PlayerService.exoPlayer?.play()
                    Log.d("xx1", App.storage.addShowed.toString())
                    Log.d("xx1", App.storage.countPopUpShowed.toString())
                    Log.d("xx1", App.storage.showPopUp.toString())

                    if (App.storage.addShowed >= App.storage.countAdd * App.storage.countPopUpShowed && App.storage.showPopUp ){
                        showPopUp()
                    }

                    destroyInterstitialAd()
                }
                override fun onAdClicked() {
                    println( ">>> onAdClicked")

                    AppsFlyerLib.getInstance().logEvent(
                        applicationContext,
                        AFInAppEventType.AD_CLICK , emptyMap()
                    )

                    // Called when a click is recorded for an ad.
                }
                override fun onAdImpression(impressionData: ImpressionData?) {
                    println( ">>> onAdImpression")

                    // Called when an impression is recorded for an ad.
                }
            })
            show(this@MainActivity)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        interstitialAdLoader?.setAdLoadListener(null)
        interstitialAdLoader = null
    }

    fun destroyInterstitialAd() {
        println(">>> destroy")
        interstitialAd?.setAdEventListener(null)
        interstitialAd = null
        loadInterstitialAd()
    }





}