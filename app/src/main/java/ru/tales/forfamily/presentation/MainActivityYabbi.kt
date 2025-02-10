//package ru.tales.forfamily.presentation
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.AppCompatImageView
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.lifecycleScope
//import com.appsflyer.AFInAppEventType
//import com.appsflyer.AppsFlyerLib
//import com.google.android.material.textview.MaterialTextView
//import com.yandex.mobile.ads.appopenad.AppOpenAd
//import com.yandex.mobile.ads.interstitial.InterstitialAd
//import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import ru.tales.forfamily.App
//import ru.tales.forfamily.R
//import ru.tales.forfamily.data.remote.ApiRepositoryImpl
//import ru.tales.forfamily.data.remote.PostBackRepositoryImpl
//import ru.tales.forfamily.domain.player.PlayerService
//import sspnet.tech.core.AdPayload
//import sspnet.tech.core.InitializationListener
//import sspnet.tech.core.InterstitialListener
//import sspnet.tech.unfiled.AdException
//import sspnet.tech.yabbi.Yabbi
//import java.math.BigInteger
//import java.security.MessageDigest
//import java.util.logging.Logger
//
//
//class MainActivity : AppCompatActivity() {
//
//    private var appOpenAd: AppOpenAd? = null
//    private var interstitialAdLoader: InterstitialAdLoader? = null
//    private var interstitialAd: InterstitialAd? = null
//    private var isAdShowedOnColdStart = false
//    private lateinit var viewModel: ViewModel
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        val decorView = window.decorView
//        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_FULLSCREEN)
//        decorView.systemUiVisibility = uiOptions
//        startService(Intent(this, PlayerService::class.java))
//        viewModel = ViewModelProvider(this)[ViewModel::class.java]
//        viewModel.onUserAlertCallback = {
//            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
//        }
//
//        lifecycleScope.launch(Dispatchers.IO) {
//            val count = ApiRepositoryImpl().getSettings()
//            if(count != 20 && count != App.storage.countAdd) {
//                with(App.storage) {
//                    addShowed -= (countPopUpShowed - 1) * countAdd
//                    addShowed = maxOf(addShowed, 0)
//                    countAdd = count
//                    countPopUpShowed = 1
//
//                    if (isTackingUserId) {
//                        if (trackingTypeUserId == 1) {
//                            addShowedUserId -= (countPostSend - 1) * countAdd
//                            addShowedUserId = maxOf(addShowedUserId, 0)
//                            countAdd = count
//                            countPostSend = 1
//                        }
//                    }
//                }
//            }
//        }
//
//        Yabbi.initialize("65057899-a16a-4877-989b-38c432a7fa15", object : InitializationListener {
//            override fun onInitializeSuccess() {
//                Log.d("TAGGG", "onInitializeSuccess")
//                Yabbi.loadAd(this@MainActivity, Yabbi.INTERSTITIAL, "b8359c60-9bde-47c9-85ff-3c7afd2bd982");
//            }
//
//            override fun onInitializeFailed(error: AdException?) {
//                Log.d("TAGGG", "errr")
//            }
//        })
//        Yabbi.enableDebug(true);
//
//        Yabbi.setInterstitialListener(object: InterstitialListener {
//            override fun onInterstitialLoaded(p0: AdPayload?) {
//                Log.d("TAGGGGAA", "onInterstitialLoaded")
//            }
//
//            override fun onInterstitialLoadFail(p0: AdPayload?, p1: AdException?) {
//                Yabbi.loadAd(this@MainActivity, Yabbi.INTERSTITIAL, "b8359c60-9bde-47c9-85ff-3c7afd2bd982")
//            }
//
//            override fun onInterstitialShown(p0: AdPayload?) {
//                // Called when ad is shown.
//                println( ">>> onAdShown")
//                App.storage.addShowed += 1
//                if (App.storage.isTackingUserId){
//                    App.storage.addShowedUserId += 1
//                }
//
//                Log.d("add-counter", App.storage.addShowed.toString())
//
//                AppsFlyerLib.getInstance().logEvent(
//                    applicationContext,
//                    AFInAppEventType.AD_VIEW , emptyMap()
//                )
//            }
//
//            override fun onInterstitialShowFailed(p0: AdPayload?, p1: AdException?) {
//                Yabbi.loadAd(this@MainActivity, Yabbi.INTERSTITIAL, "b8359c60-9bde-47c9-85ff-3c7afd2bd982");
//            }
//
//            override fun onInterstitialClosed(p0: AdPayload?) {
//                println( ">>> onAdDismissed")
//                println( ">>> try play")
//                PlayerService.exoPlayer?.play()
//
//                if (App.storage.addShowed >= App.storage.countAdd * App.storage.countPopUpShowed && App.storage.showPopUp ){
//                    showPopUp()
//                }
//                if (App.storage.trackingTypeUserId == 1) {
//                    if (App.storage.addShowedUserId >= App.storage.countAdd * App.storage.countPostSend){
//                        lifecycleScope.launch(Dispatchers.IO){
//                            val success = PostBackRepositoryImpl().postUserId(App.storage.userId)
//                            if (success) {
//                                App.storage.countPostSend += 1
//                            }
//                            Log.d("TAG", "success $success")
//
//                        }
//                    }
//                }else{
//                    if (App.storage.addShowedUserId >= App.storage.countAdd && App.storage.countPostSend == 1){
//                        lifecycleScope.launch(Dispatchers.IO){
//                            val success = PostBackRepositoryImpl().postUserId(App.storage.userId)
//                            if (success) {
//                                App.storage.countPostSend += 1
//                            }
//                            Log.d("TAG", "success $success")
//
//                        }
//                    }
//                }
//                destroyAd()
//
//            }
//
//        })
//    }
//
//    fun showAd() {
//        Log.d("TAG", "showAd")
//        if (Yabbi.isAdLoaded(Yabbi.INTERSTITIAL, "b8359c60-9bde-47c9-85ff-3c7afd2bd982")) {
//            Log.d("TAG", "showAd2")
//            Yabbi.showAd(this@MainActivity, Yabbi.INTERSTITIAL, "b8359c60-9bde-47c9-85ff-3c7afd2bd982")
//        }else{
//            Log.d("TAG", "showAd3")
//            Yabbi.loadAd(this@MainActivity, Yabbi.INTERSTITIAL, "b8359c60-9bde-47c9-85ff-3c7afd2bd982");
//        }
//
//    }
//    fun destroyAd() {
//        Yabbi.destroyAd(Yabbi.INTERSTITIAL, "b8359c60-9bde-47c9-85ff-3c7afd2bd982")
//        Yabbi.loadAd(this@MainActivity, Yabbi.INTERSTITIAL, "b8359c60-9bde-47c9-85ff-3c7afd2bd982");
//    }
//
//
//    private fun showPopUp() {
//        if (viewModel.mode.value == Mode.DEFAULT_MODE) {
//            if (PlayerService.exoPlayer?.isPlaying == true) {
//                PlayerService.exoPlayer?.pause()
//            }
//
//            val builder = AlertDialog.Builder(this)
//
//            val customLayout: View = layoutInflater.inflate(R.layout.fragment_pop_up, null)
//            builder.setView(customLayout)
//            val dialog = builder.create()
//
//            val hash = md5()
//
//            val textHash = customLayout.findViewById<MaterialTextView>(R.id.materialTextView3)
//            val btnClose = customLayout.findViewById<AppCompatImageView>(R.id.close)
//
//            textHash.setText(hash)
//
//            btnClose.isClickable = false
//            lifecycleScope.launch(Dispatchers.IO) {
//                val isSuccessful = ApiRepositoryImpl().postData(hash)
//                withContext(Dispatchers.Main) {
//                    if (isSuccessful) {
//                        Toast.makeText(this@MainActivity, "Успешно", Toast.LENGTH_SHORT)
//                            .show()
//                        btnClose.isClickable = true
//                        App.storage.countPopUpShowed += 1
////                        dialog.dismiss()
//                    } else {
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Произошла ошибка",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        btnClose.isClickable = true
////                        dialog.dismiss()
//                    }
//                }
//
//
//            }
//
//
//            btnClose.setOnClickListener { dialog.dismiss() }
//
//
//
//            dialog.show()
//            if (dialog.isShowing){
//                App.storage.countPopUpShowed += 1
//            }
//        }
//
//    }
//
//    fun getRandomString(length: Int) : String {
//        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
//        return (1..length)
//            .map { allowedChars.random() }
//            .joinToString("")
//    }
//    fun md5(): String {
//        val input = getRandomString(18)
//
//        val md = MessageDigest.getInstance("MD5")
//        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Yabbi.destroyAd(Yabbi.INTERSTITIAL, "b8359c60-9bde-47c9-85ff-3c7afd2bd982")
//    }
//
//
//
//
//
//}