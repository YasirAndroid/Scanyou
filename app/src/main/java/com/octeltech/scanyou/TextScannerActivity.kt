package com.octeltech.scanyou

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.ClipboardManager
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_qr_code_result.*
import kotlinx.android.synthetic.main.activity_text_scanner.*
import java.io.File
import java.io.IOException

class TextScannerActivity : AppCompatActivity() {

    lateinit var bitmap: Bitmap
    lateinit var bitmap1: Bitmap
    lateinit var imageView: ImageView
    lateinit var path: Uri
    lateinit var prog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_scanner)

        prog = ProgressDialog(this)
        imageView = findViewById(R.id.iv_text_image)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED )
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 0)

            else
                init()

        } else {
                init()
        }

    }

    fun init(){

        btn_pick.setOnClickListener() {
            val i = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(i, 1)
        }

        btn_scan_text.setOnClickListener(){
            prog.setTitle("Scanning Text")
            prog.setMessage("Please wait..")
            prog.show()
            if (imageView.drawable!=null) {

                val image: FirebaseVisionImage
                try {

                    image = FirebaseVisionImage.fromFilePath(this, path)

                    val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

                    detector.processImage(image)
                        .addOnSuccessListener { firebaseVisionText ->
                            // Task completed successfully
                            // ...
                            val resultText = firebaseVisionText.text
                            val intent = Intent(this, QrCodeResultActivity::class.java)
                            intent.putExtra("result", resultText)
                            prog.dismiss()
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            // Task failed with an exception
                            // ...
                            prog.dismiss()
                            Toast.makeText(this, "Cant Scan Result", Toast.LENGTH_LONG).show()
                        }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            else {
                Toast.makeText(this, "Please pick an image first", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK &&   null!=data  ) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            val cursor = contentResolver.query(
                selectedImage!!,
                filePathColumn, null, null, null
            )
            cursor!!.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()

            //bitmap = BitmapFactory.decodeFile(picturePath)
            path = selectedImage
            Log.e("000","$selectedImage")

            Picasso.get().load(selectedImage).into(imageView)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED&&grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED&&grantResults.isNotEmpty() && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    init()
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