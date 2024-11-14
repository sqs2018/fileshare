package com.sqs.resourceshare_android;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.sqs.resourceshare_android.databinding.ActivityScanBinding;
import com.sqs.resourceshare_android.ui.download.DownloadFragment;

import java.util.List;


public class ScanActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        barcodeView = new DecoratedBarcodeView(this);
        setContentView(barcodeView);

        barcodeView.decodeContinuous(callback);
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                // 扫描到二维码，处理结果
                handleDecodeInternally(result.getText());
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    private void handleDecodeInternally(String code) {
        // 处理扫描结果，例如跳转到另一个Activity显示结果
        Intent intent = new Intent(this, DownloadFragment.class);
        intent.putExtra("data", code);
        setResult(RESULT_OK, intent);
        //startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //barcodeView.stop();
    }
}