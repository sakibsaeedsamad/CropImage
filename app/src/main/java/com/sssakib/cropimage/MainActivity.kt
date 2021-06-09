package com.sssakib.cropimage

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView.setOnClickListener {



            selectImage()

        }

    }

    private fun selectImage() {

        try {

            val options =
                arrayOf<CharSequence>("Take Photo", "Choose From Gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select Option")
            builder.setItems(options,
                DialogInterface.OnClickListener { dialog, item ->
                    if (options[item] == "Take Photo") {
                        dialog.dismiss()
                        requestCameraPermission()
                    } else if (options[item] == "Choose From Gallery") {
                        dialog.dismiss()
                        requestStoragePermission()
                    } else if (options[item] == "Cancel") {
                        dialog.dismiss()
                    }
                })
            builder.show()
        } catch (e: Exception) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun requestStoragePermission() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {


                        openGallary()
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun requestCameraPermission() {

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {

                        openCamera()

                     }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun showSettingsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
            openSettings()
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.show()
    }
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }


    private fun openCamera() {

//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//
//        startActivityForResult(intent, 7)

        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAutoZoomEnabled(true)
            .setOutputCompressFormat(Bitmap.CompressFormat.PNG)
            .setAspectRatio(4, 4)
            .start(this@MainActivity)
    }


    private fun openGallary() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//        startActivityForResult(intent, 100)
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAutoZoomEnabled(true)
            .setOutputCompressFormat(Bitmap.CompressFormat.PNG)
            .setAspectRatio(4, 4)
            .start(this@MainActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result?.uri
                imageView.setImageURI(resultUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result?.error
            }
        }
    }


}