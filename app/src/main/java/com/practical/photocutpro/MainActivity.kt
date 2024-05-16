package com.practical.photocutpro

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.practical.photocutpro.adapter.ViewPagerAdapter
import com.practical.photocutpro.databinding.ActivityMainBinding
import com.practical.photocutpro.ui.ImageFragment
import com.practical.photocutpro.ui.VideoFragment
import com.practical.photocutpro.viewmodel.ImageViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val PICK_IMAGES_REQUEST_CODE = 1
    private val PICK_VIDEO_REQUEST_CODE = 2
    lateinit var imageFilePath : File
    lateinit var videoFilePath : File

    private val VIDEO_CAPTURE = 101
    private val IMAGE_CAPTURE = 102
    var vFilename: String = ""



    companion object{
        lateinit var viewModel: ImageViewModel
    }


    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(VideoFragment(), "Video")
        adapter.addFragment(ImageFragment(), "Image")
        binding.viewPager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPager)


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDefaultDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.lines)

        binding.flButton.setOnClickListener {
            binding.llView.isVisible = !binding.llView.isVisible
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onAllow13Permission()
        } else {
            onAllowPermission()
        }


        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[ImageViewModel::class.java]


        binding.tvCamera.setOnClickListener {

            val currentFragment = adapter.getFragment(binding.viewPager.currentItem)

            if (currentFragment != null) {

                if (currentFragment.javaClass.simpleName.equals("VideoFragment")) {

                    val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                    startActivityForResult(intent, VIDEO_CAPTURE)

                }
                if (currentFragment.javaClass.simpleName.equals("ImageFragment")) {


                    openCamera()

                }
            } else {
                Log.d("CurrentFragment", "No fragment found")
            }

            binding.llView.isVisible = false

        }
        binding.tvGallery.setOnClickListener {

            val currentFragment = adapter.getFragment(binding.viewPager.currentItem)

            if (currentFragment != null) {

                if (currentFragment.javaClass.simpleName.equals("VideoFragment")) {
                    openGalleryForVideos()
                }
                if (currentFragment.javaClass.simpleName.equals("ImageFragment")) {
                    openGalleryForImages()

                }

            } else {
                Log.d("CurrentFragment", "No fragment found")
            }

            binding.llView.isVisible = false
        }



        val path : String = "$cacheDir/image"

         imageFilePath = File(path)
        if (!imageFilePath.exists()){
            imageFilePath.mkdir()
        }

        val path2 : String = "$cacheDir/video"
        videoFilePath = File(path2)
        if (!videoFilePath.exists()){
            videoFilePath.exists()
        }


    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        vFilename = "FOTO_" + timeStamp + ".jpg"


        val dtr =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        val file = File(dtr, vFilename);
        val image_uri = FileProvider.getUriForFile(this, this.applicationContext.packageName + ".provider", file);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE)
    }



    private fun openGalleryForImages() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGES_REQUEST_CODE)
    }

    private fun openGalleryForVideos() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "video/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_VIDEO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGES_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            handleSelectedImages(data , PICK_IMAGES_REQUEST_CODE)
        }

        if (requestCode == PICK_VIDEO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            handleSelectedImages(data , PICK_VIDEO_REQUEST_CODE)
        }


        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {

                val dtr =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val file = File(dtr, vFilename);
                val uri = FileProvider.getUriForFile(this, this.applicationContext.packageName + ".provider", file);
                copyImageToFolder(this , uri , imageFilePath.absolutePath , 1)

                Log.d("CurrentFragment", "No fragment uri${uri}")


            } else if (resultCode == Activity.RESULT_CANCELED) {

                Toast.makeText(this, "Image capture cancelled.", Toast.LENGTH_LONG).show()
            } else {

                Toast.makeText(this, "Failed to capture Image", Toast.LENGTH_LONG).show()
            }
        }

        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {

                Toast.makeText(this, "Video saved to:\n" + data!!.data, Toast.LENGTH_LONG).show()
                handleSelectedImages(data , 2)
            } else if (resultCode == Activity.RESULT_CANCELED) {

                Toast.makeText(this, "Video recording cancelled.", Toast.LENGTH_LONG).show()
            } else {

                Toast.makeText(this, "Failed to record video", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun handleSelectedImages(data: Intent? , item : Int) {
        val clipData = data?.clipData
        val selectedImages = mutableListOf<Uri>()

        if (clipData != null) {
            for (i in 0 until clipData.itemCount) {
                val imageUri = clipData.getItemAt(i).uri
                selectedImages.add(imageUri)
            }
        } else {
            data?.data?.let {
                selectedImages.add(it)
            }
        }

        if (item == 1){
            selectedImages.forEach { uri ->

                copyImageToFolder(this , uri , imageFilePath.absolutePath , 1)

            }
        }else{
            selectedImages.forEach { uri ->

                copyImageToFolder(this , uri , videoFilePath.absolutePath ,  2)

            }
        }

    }

    fun copyImageToFolder(context: Context, imageUri: Uri, destinationFolder: String , item : Int) {
        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(imageUri)

        val destinationDir = File(destinationFolder)
        if (!destinationDir.exists()) {
            destinationDir.mkdirs()
        }

        val fileName = getFileName(contentResolver, imageUri)
        val destinationFile = File(destinationDir, fileName)

        inputStream?.use { input ->
            FileOutputStream(destinationFile).use { output ->
                val buffer = ByteArray(1024)
                var length: Int
                while (input.read(buffer).also { length = it } > 0) {
                    output.write(buffer, 0, length)

                }
            }
        }

        if (item==1){
            viewModel.loadVideo()

        }else{
            viewModel.loadImages()
        }

    }

    fun getFileName(contentResolver: ContentResolver, uri: Uri): String {
        var name = ""
        val returnCursor = contentResolver.query(uri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }


    private fun onAllow13Permission() {
        TedPermission.create()
            .setPermissionListener(permissionlistener)
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(READ_MEDIA_IMAGES , CAMERA , READ_MEDIA_VIDEO)
            .check()
    }

    private fun onAllowPermission() {
        TedPermission.create()
            .setPermissionListener(permissionlistener)
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE ,CAMERA)
            .check()
    }

    private val permissionlistener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {

        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
            settingActivityOpen()

        }
    }

    private fun settingActivityOpen() {
        val i = Intent()
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:$packageName")
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        startActivity(i)
    }


}