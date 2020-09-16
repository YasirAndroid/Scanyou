package com.octeltech.scanyou

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class BarCodeActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var mScannerView: ZXingScannerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA), 0)
        else
            mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mScannerView = ZXingScannerView(this)
                    setContentView(mScannerView)
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
    override fun handleResult(rawResult: Result) {

        /*
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Scan Result")
        builder.setMessage(rawResult.text)
        val alert1 = builder.create()
        alert1.show()
        */

        val intent = Intent(this, QrCodeResultActivity::class.java)
        intent.putExtra("result",rawResult.text)

        startActivity(intent)

        // If you would like to resume scanning, call this method below:
        mScannerView?.resumeCameraPreview(this)
    }

    public override fun onResume() {
        super.onResume()
        // Register ourselves as a handler for scan results.
        mScannerView?.setResultHandler(this)
        // Start camera on resume
        mScannerView?.startCamera()
    }

    public override fun onPause() {
        super.onPause()
        // Stop camera on pause
        mScannerView?.stopCamera()
    }
}