package com.octeltech.scanyou

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doc_scanner_btn.setOnClickListener(){
            startActivity(Intent(this, DocScannerActivity::class.java))
        }
        qr_scanner_btn.setOnClickListener(){
           startActivity(Intent(this, QRCodeScannerActivity::class.java))
        }
        bar_scanner_btn.setOnClickListener(){
            startActivity(Intent(this, BarCodeActivity::class.java))
        }
        text_scanner_btn.setOnClickListener(){
            startActivity(Intent(this, TextScannerActivity::class.java))
        }
    }
}