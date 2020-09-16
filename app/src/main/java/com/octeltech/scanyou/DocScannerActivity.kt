package com.octeltech.scanyou

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import com.bumptech.glide.Glide
import com.scanlibrary.ScanActivity
import com.scanlibrary.ScanConstants
import com.scanlibrary.ScanConstants.OPEN_CAMERA
import com.scanlibrary.ScanConstants.OPEN_MEDIA
import kotlinx.android.synthetic.main.activity_doc_scanner.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.DecimalFormat

@Suppress("DEPRECATED_IDENTITY_EQUALS", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DocScannerActivity : AppCompatActivity() {

    var uri: Uri? = null
    var bitmap: Bitmap? = null
    var imageSize: Double = 0.0
    var rotate = 90f
    var h: String = ""
    var w: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        else
        setContentView(R.layout.activity_doc_scanner)

        image_rotate_btn.setOnClickListener(){
            if (bitmap==null){
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
            }
            else {
                scannedImageView.setImageBitmap(RotateBitmap(bitmap!!, rotate))
                if (rotate <= 359) {
                    rotate += 90f
                } else {
                    rotate = 90f
                }
            }
        }
        resize_image_btn.setOnClickListener(){
            showCustomDialog()
        }
        btn_save_image.setOnClickListener(){

            var currentTime = (System.currentTimeMillis()).toString()

            val file_path = Environment.getExternalStorageDirectory().absolutePath + "/Scanyou"
            val dir = File(file_path)
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "IMG_$currentTime.png")
            try {
                val fOut = FileOutputStream(file)
                bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                fOut.flush()
                fOut.close()
                Toast.makeText(this, "Image Saved",Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("ye baat h",e.toString())
            }
        }
    }

    fun openCamera(v: View){
        val REQUEST_CODE = 99
        val preference: Int = ScanConstants.OPEN_CAMERA
        val intent = Intent(this, ScanActivity::class.java)
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, OPEN_CAMERA)
        startActivityForResult(intent, REQUEST_CODE)
    }
    fun openGallery(v: View){
        val REQUEST_CODE = 99
        val preference: Int = ScanConstants.OPEN_MEDIA
        val intent = Intent(this, ScanActivity::class.java)
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, OPEN_MEDIA)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 99 && resultCode === Activity.RESULT_OK) {
            uri = data!!.getParcelableExtra(ScanConstants.SCANNED_RESULT)
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                imageSize = (bitmap!!.byteCount/1024.0)
                if (imageSize>=1024) {
                    imageSize /= 1024
                    tv_image_size_inkb.text = (DecimalFormat("##.#").format(imageSize) + "MB").toString()
                }
                else {
                    tv_image_size_inkb.text = (DecimalFormat("##.#").format(imageSize) + "KB").toString()
                }
                contentResolver.delete(uri!!, null, null)
                Glide.with(this)
                    .load(bitmap)
                    .fitCenter()
                    .into(scannedImageView)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    fun RotateBitmap(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source,
            0,
            0,
            source.width,
            source.height,
            matrix,
            true
        )
    }
    private fun showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        val viewGroup = findViewById<ViewGroup>(R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView: View =
            LayoutInflater.from(this).inflate(R.layout.resize_layout_dialog, viewGroup, false)


        //Now we need an AlertDialog.Builder object
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)
        var height = dialogView.findViewById<EditText>(R.id.et_height)
        var width = dialogView.findViewById<EditText>(R.id.et_width)
        var cancelbtn = dialogView.findViewById<TextView>(R.id.tv_cancel)
        var resizebtn = dialogView.findViewById<TextView>(R.id.tv_resize)
        //finally creating the alert dialog and displaying it
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

        resizebtn.setOnClickListener(){
             h = height.text.toString()
             w = width.text.toString()
            try {
                var scaledBitmap = bitmap!!.scale(w.toInt(), h.toInt(), true)
                imageSize = (scaledBitmap.byteCount/1024.0)
                if (imageSize>=1024) {
                    imageSize /= 1024
                    tv_image_size_inkb.text = (DecimalFormat("##.#").format(imageSize) + "MB").toString()
                }
                else {
                    tv_image_size_inkb.text = (DecimalFormat("##.#").format(imageSize) + "KB").toString()
                }
                contentResolver.delete(uri!!, null, null)
                scannedImageView.setImageBitmap(RotateBitmap(scaledBitmap,90f))
                bitmap = scaledBitmap
                alertDialog.hide()

            } catch (e: IOException) {
                e.printStackTrace()
                alertDialog.hide()
            }
            alertDialog.hide()
        }
        cancelbtn.setOnClickListener() {
            alertDialog.dismiss()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setContentView(R.layout.activity_doc_scanner)
                }
                else{
                    Toast.makeText(
                        applicationContext,
                        "Camera Permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}