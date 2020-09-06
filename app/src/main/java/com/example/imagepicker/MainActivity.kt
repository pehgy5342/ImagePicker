package com.example.imagepicker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    val requestImageCapture = 1
    val requestReadAlbum = 2
    val requestCode = 100
    var photoPath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Glide.with(this).load(R.drawable.ic_image_black_24dp).into(iv_image)




        btn_camera.setOnClickListener {


            getPermission()

        }

        btn_album.setOnClickListener {

            getPermission()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            requestImageCapture -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {


                        Glide.with(this).load(photoPath).into(iv_image)

                    }
                    Activity.RESULT_CANCELED -> {
                    }

                }
            }
            requestReadAlbum -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val uri = data!!.data
//                        iv_image.setImageURI(uri)

                        Glide.with(this).load(uri).into(iv_image)

                    }
                    Activity.RESULT_CANCELED -> {

                    }
                }
            }
        }

    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {


        val perms = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {

            AppSettingsDialog.Builder(this)
                .setTitle("警告")
                .setRationale("沒開啟權限將無法使用部分功能\n\n請至設定→應用程式→權限 開啟")
                .setPositiveButton("OK")
                .setNegativeButton("取消").build().show()

        } else {

            Toast.makeText(this, "請開啟授權", Toast.LENGTH_SHORT).show()

        }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

        btn_camera.setOnClickListener {


            takePicture()

        }

        btn_album.setOnClickListener {

            readAlbum()
        }
    }

    fun getPermission() {

        if (EasyPermissions.hasPermissions(
                this,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {

            btn_camera.setOnClickListener {


                takePicture()

            }

            btn_album.setOnClickListener {

                readAlbum()
            }


        } else {
            EasyPermissions.requestPermissions(
                this,
                "沒授權無法拍照與開啟相簿喔~",
                requestCode,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

        }

    }


    fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val tmpFile =
            File(Environment.getExternalStorageDirectory().toString(), System.currentTimeMillis().toString() + ".jpg")
        val uriForCamera = FileProvider.getUriForFile(this, "com.example.imagepicker.fileprovider", tmpFile)

        photoPath = uriForCamera
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForCamera)
        startActivityForResult(intent, requestImageCapture)
    }

    fun readAlbum() {

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
//        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, requestReadAlbum)


    }


}
