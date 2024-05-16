package com.practical.photocutpro.ui

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.practical.photocutpro.R
import com.practical.photocutpro.databinding.ActivityImageShowBinding
import java.io.File
import java.io.IOException

class ImageShowActivity : AppCompatActivity() {
    lateinit var binding : ActivityImageShowBinding
    lateinit var path : String

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageShowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDefaultDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.lines)

        path = intent.getStringExtra("path").toString()

        Glide.with(this)
            .load(path)
            .into(binding.viewPager)

        binding.btnSetWallpaper.setOnClickListener {

            val bitmap: Bitmap
            try {
                bitmap = BitmapFactory.decodeFile(path)
                SetWallpaper(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun SetWallpaper(bitmap: Bitmap){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                val wallpaperManager = WallpaperManager.getInstance(this@ImageShowActivity)
                try {
                    wallpaperManager.setBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Handler(Looper.getMainLooper()).post {
                        try {
                            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                Toast.makeText(
                    this@ImageShowActivity,
                    "Wallpaper applied.",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
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
                    Files()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }


    private fun Files() {

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


}