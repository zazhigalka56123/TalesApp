package ru.tales.forfamily.presentation.fragments

import android.animation.Animator
import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import ru.tales.forfamily.R
import ru.tales.forfamily.data.db.TaleRepositoryImpl
import ru.tales.forfamily.databinding.FragmentPlayerBinding
import ru.tales.forfamily.domain.Tale
import ru.tales.forfamily.domain.player.PlayerService
import ru.tales.forfamily.presentation.MainActivity
import ru.tales.forfamily.presentation.ViewModel
import ru.tales.forfamily.presentation.snackBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.Locale
import kotlin.math.min


class FragmentPlayer : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding
        get() = _binding ?: throw RuntimeException("FragmentPlayerBinding is null")

    private val taleIndex by lazy { navArgs<FragmentPlayerArgs>().value.index }
    private val stop by lazy { navArgs<FragmentPlayerArgs>().value.stop }

    private val player = PlayerService.exoPlayer
    private val options = RequestOptions().error(R.drawable.ic_launcher_background)

    private lateinit var viewModel: ViewModel

    private var backPay = false
    private var type = 0 // 0 - Player, 1 - Text

    private val onEverySecond: Runnable = object : Runnable {
        override fun run() {
            var duration = player!!.duration.toFloat()
            if (duration <= 0) duration = (60 * 60 * 1000).toFloat()
            onUpdateProgressViews(
                min(duration.toLong(), player.currentPosition),
                duration,
            )
            binding.seekbar.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
//        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]
        viewModel.list.value = viewModel.list.value

        viewModel.list.observeForever {
            player?.currentMediaItem?.mediaMetadata?.let { it1 -> updateMeta(it1) }
        }


        if (viewModel.backPay){
            backPay = true
            viewModel.backPay = false
        }

        setupPlayer()
        setupButtons()
        showAdd()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.pause()
        viewModel.onTracksChanged?.invoke()
        viewModel.onMediaMetadataChanged = null
        viewModel.onIsPlayingChanged = null
    }

    private fun setupPlayer() {
        if (player != null) {
            if (stop && !backPay) {
                player.setMediaItems(
                    viewModel.list.value?.map { it ->
                        it.toMediaItem()
                    } ?: emptyList(),
                    if (taleIndex == -1) 0 else taleIndex,
                    0
                )
            }
            if (player.playbackState == Player.STATE_IDLE) {
                player.prepare()
            }

            player.removeListener(viewModel.playerListener)
            player.addListener(viewModel.playerListener)
            player.playWhenReady = true

            player.play()
            val map = mapOf("name" to player.mediaMetadata.title)
            AppsFlyerLib.getInstance().logEvent(
                requireActivity().application,
                "af_tale_play" , map,
                object : AppsFlyerRequestListener {
                    override fun onSuccess() {
                        println( "Event2 sent successfully")
                    }
                    override fun onError(errorCode: Int, errorDesc: String) {
                        println(  "Event2 failed to be sent:" +
                                "Error code: " + errorCode + ""
                                + "Error description: " + errorDesc)
                    }
                })


            viewModel.isPlayling.value = true

            updateMeta(player.mediaMetadata, false)
        }

        viewModel.onIsPlayingChanged =
            {
                try {
                    if (it) {
                        binding.buttonPlayer.setBackgroundResource(R.drawable.shape_btn_pause)
                    } else {
                        binding.buttonPlayer.setBackgroundResource(R.drawable.shape_btn_play)
                    }
                } catch (_: Exception) {
                }
            }

        viewModel.onMediaMetadataChanged = {
            try {
                updateMeta(it)
            } catch (_: Exception) {
            }
        }
        viewModel.onTracksChanged = {
            showAdd()

        }
    }

    private fun showAdd(){
        try {
            println(">>>show add")
            player?.pause()
            println(">>> pause!")
            viewModel.isPlayling.value = true
            binding.buttonPlayer.setBackgroundResource(R.drawable.shape_btn_play)
            (activity as MainActivity).showAd()
            val title = player?.mediaMetadata?.title
            if (title != null) {
                val map = mapOf("name" to title)
                AppsFlyerLib.getInstance().logEvent(
                    requireActivity().application,
                    "af_tale_play", map,
                    object : AppsFlyerRequestListener {
                        override fun onSuccess() {
                            println("Event2 sent successfully")
                        }

                        override fun onError(errorCode: Int, errorDesc: String) {
                            println(
                                "Event2 failed to be sent:" +
                                        "Error code: " + errorCode + ""
                                        + "Error description: " + errorDesc
                            )
                        }
                    })
            }
        } catch (_: Exception) {
        }
    }

    private fun playPause() {
        if (player?.isPlaying == true) {
            player.pause()
            println(">>> pause!")
            viewModel.isPlayling.value = false
            binding.buttonPlayer.setBackgroundResource(R.drawable.shape_btn_play)
        } else {
            println(">>> play!")
            player?.play()
            viewModel.isPlayling.value = true
            binding.buttonPlayer.setBackgroundResource(R.drawable.shape_btn_pause)
        }
    }

    private fun updateMeta(mediaMetadata: MediaMetadata, flag: Boolean = true) {
        if (!flag){
            println(">>>pauses")
            playPause()
        }
        val item = viewModel.list.value?.get(mediaMetadata.extras?.getInt("index")?: 0)
        Glide.with(binding.ivCard).load(item?.img).apply(options).into(binding.ivCard)
        Glide.with(binding.ivTaleText).load(item?.img).apply(options).into(binding.ivTaleText )

        binding.tvNameTalePlayer.text = mediaMetadata.title
        binding.tvNameTaleText.text = mediaMetadata.title

        binding.tvTextTale.text = item?.text

        item?.let { updateDownloadIcon(it) }

    }

    private fun updateDownloadIcon(it: Tale){
        if (it.isSaved){
            binding.ivDownload.setImageResource(R.drawable.ic_downloaded)
        }else{
            binding.ivDownload.setImageResource(R.drawable.ic_download)
        }
    }
    //View
    private fun setupButtons() {
        Glide.with(binding.ivCard).load(viewModel.list.value?.get(taleIndex)?.img).apply(options).into(binding.ivCard)

        binding.cardBack.setOnClickListener { findNavController().navigateUp() }
        binding.buttonPlayer.setOnClickListener { playPause() }
        binding.btnNextAudio.setOnClickListener { player?.seekToNext() }
        binding.btnPrevAudio.setOnClickListener { player?.seekToPrevious() }
        binding.seekbar.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                player?.seekTo(value.toLong())
            }
        }
//        binding.ivDownload.setOnClickListener{
//            val tale =
//                viewModel.list.value?.get(player?.currentMediaItem?.mediaMetadata?.extras?.getInt("index")?: 0)
//            if (tale != null) {
//                lifecycleScope.launch(Dispatchers.IO) {
//                    if (!tale.isSaved) {
//                        downloadTale(tale)
//                    }else {
//                        withContext(Dispatchers.Main) {
//                            requireContext().snackBar("Уже скачана")
//                        }
//                    }
//                }
//            }
//        }
        binding.seekbar.postDelayed(onEverySecond, 1000)

        binding.btnType.setOnClickListener {
            if (type == 0){
                binding.playerLayout.visibility = View.GONE
                binding.viewText.visibility = View.VISIBLE
                binding.btnType.setImageResource(R.drawable.ic_player_type)
                player?.pause()
                type = 1
            }else{
                binding.playerLayout.visibility = View.VISIBLE
                binding.viewText.visibility = View.GONE
                binding.btnType.setImageResource(R.drawable.ic_text_tale)
                type = 0
                val map = mapOf("name" to player?.mediaMetadata?.title)
                AppsFlyerLib.getInstance().logEvent(
                    requireActivity().application,
                    "af_tale_text" , map
                )
            }
        }
    }

    //Seekbar
    fun onUpdateProgressViews(progresss: Long, total: Float) {
        val progress = maxOf(progresss, 0)
        println("!!! ${progress.toFloat()}")
        val animator = ObjectAnimator.ofFloat(binding.seekbar, "value", progress.toFloat())
        animator.duration = 400
        animator.interpolator = LinearInterpolator()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator) {
                binding.seekbar.valueTo = total
            }

            override fun onAnimationStart(p0: Animator) {}
            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}
        })
        animator.start()
        binding.tvTimeNow.text = getReadableDurationString(progress)
        binding.tvTimeEnd.text = getReadableDurationString(total.toLong())
    }

    private fun getReadableDurationString(songDurationMillis: Long): String {
        var minutes = songDurationMillis / 1000 / 60
        val seconds = songDurationMillis / 1000 % 60
        return if (minutes < 60) {
            String.format(
                Locale.getDefault(),
                "%01d:%02d",
                minutes,
                seconds
            )
        } else {
            minutes %= 60
            String.format(
                Locale.getDefault(),
                "%01d:%02d",
                minutes,
                seconds
            )
        }
    }

//    private suspend fun downloadTale(tale: Tale) {
//        try {
//            fun download(): String = run {
//                URL(tale.uri).openStream().use { input ->
//                    val file = File(context?.filesDir, Uri.parse(tale.uri).lastPathSegment.toString())
//                    FileOutputStream(file).use { output ->
//                        input.copyTo(output)
//                    }
//                    return file.absolutePath
//                }
//            }
//
//
//            withContext(Dispatchers.Main) {
//                requireContext().snackBar("Начинаем скачивать сказку \"${tale.name}\"")
//            }
//            val path = download()
//            with(viewModel.list.value?.get(tale.index)){
//                this?.uri = path
//                this?.isSaved = true
//            }
//
//            TaleRepositoryImpl(requireActivity().application)
//                .addTale(tale)
//
//            withContext(Dispatchers.Main) {
//                player?.currentMediaItem?.mediaMetadata?.let { updateMeta(it) }
//                requireContext().snackBar("Сказка \"${tale.name}\" успешно загружена")
//            }
//        } catch (exc: Exception) {
//            exc.printStackTrace()
//            withContext(Dispatchers.Main) {
//                requireContext().snackBar("Не удалось загрузить сказку \"${tale.name}\"")
//            }
//            return
//        }
//    }

}