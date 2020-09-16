package com.octeltech.scanyou

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.ClipboardManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_qr_code_result.*

class QrCodeResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_result)

        var intent = intent
        intent.getStringExtra("result")

        tv_result.text = intent.getStringExtra("result")

        btn_copy_text.setOnClickListener(){
            val cm = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.text = tv_result.text
            Toast.makeText(this, "Text Copied", Toast.LENGTH_SHORT).show()
        }

    }
}