package com.practical.photocutpro.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.practical.photocutpro.R
import com.practical.photocutpro.databinding.ActivityVideoPlayerBinding
import com.practical.photocutpro.viewmodel.ImageViewModel
import java.io.File

class VideoPlayerActivity : AppCompatActivity() {

    lateinit var binding : ActivityVideoPlayerBinding

    private val handler = Handler()
    private lateinit var videoFiles: List<File>
    private var currentVideoIndex = 0
    private lateinit var viewModel: ImageViewModel

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDefaultDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.lines)

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(this.application))[ImageViewModel::class.java]
        val videoPaths = intent.getStringArrayListExtra("VIDEO_PATHS") ?: arrayListOf()
        currentVideoIndex = intent.getIntExtra("position" , 0)
        videoFiles = videoPaths.map { File(it) }

        initializePlayer()


        binding.btnPlayPause.setOnClickListener {
            if (viewModel.player!!.isPlaying) {
                viewModel.player!!.pause()
                binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
            } else {
                viewModel.player!!.play()
                binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
            }
        }

        binding.btnNext.setOnClickListener {
            currentVideoIndex = (currentVideoIndex + 1) % videoFiles.size
            playVideo()
        }

        binding.btnPrevious.setOnClickListener {
            currentVideoIndex = if (currentVideoIndex - 1 < 0) videoFiles.size - 1 else currentVideoIndex - 1
            playVideo()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.player!!.seekTo((progress * viewModel.player!!.duration / 100).toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        viewModel.player!!.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY || state == Player.STATE_BUFFERING) {
                    updateSeekBar()
                }
            }
        })

        playVideo()
    }

    private fun initializePlayer() {
        if (viewModel.player == null) {
            viewModel.player = ExoPlayer.Builder(this).build()
        }
        binding.playerView.player = viewModel.player
        viewModel.player?.playWhenReady = true
    }


    private fun playVideo() {
        viewModel.player!!.setMediaItem(MediaItem.fromUri(Uri.fromFile(videoFiles[currentVideoIndex])))
        viewModel.player!!.prepare()
        viewModel.player!!.play()
        binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
    }

    private fun updateSeekBar() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                binding.seekBar.progress = (viewModel.player!!.currentPosition * 100 / viewModel.player!!.duration).toInt()
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.player!!.release()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.delete_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                showExpandedMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showExpandedMenu() {
        val popupMenu = PopupMenu(this, findViewById(R.id.action_settings))
        popupMenu.menuInflater.inflate(R.menu.menu_main_expanded, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_delete -> {
                    Files(videoFiles[currentVideoIndex].absolutePath)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }


    private fun Files(path : String) {

        val file = File(path)

        if (file.exists()) {
            val deleted = file.delete()
            MediaScannerConnection.scanFile(
                applicationContext, arrayOf(file.absolutePath), null, null
            )
            Toast.makeText(this, "File Delete", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.player?.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        viewModel.player?.playWhenReady = false
    }


}
