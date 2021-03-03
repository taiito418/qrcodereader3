package com.itokikaku.qrcodereader3;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

/**
 * Sample Activity extending from ActionBarActivity to display a Toolbar.
 */
public class ToolbarCaptureActivity extends AppCompatActivity {
    private CaptureManager capture;
    private CompoundBarcodeView barcodeScannerView;
    private MyConfirmDialog dialogFragment;

    /// リクエストコードを定義
    private static final int REQUESTCODE_SNS_SHARE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.capture_appcompat);
        setContentView(R.layout.capture_appcompat_custom);
        //Toolbar toolbar = findViewById(R.id.my_awesome_toolbar);
        //toolbar.setTitle("Scan Barcode");
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView = findViewById(R.id.qrcode_reader); // カスタムQR

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        //capture.decode(); // キャプチャして、呼び出し元のActivityに返す

        barcodeViewSingle();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE_SNS_SHARE) {
            if (resultCode == RESULT_OK) {
                /// シェアしてくれた人に特典付与したり、
                /// お礼のメッセージを表示したり、、、
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        barcodeViewSingle();
    }

    public void barcodeViewSingle() { //読み取り処理を行うメソッド
        barcodeScannerView.decodeSingle(new BarcodeCallback() { //読み取りを行う
            @Override
            public void barcodeResult(BarcodeResult result) {
                //okDialog(result.getText()); //result.getText()で読み取り結果を取得し、ダイアログのメソッドへ
                //Toast.makeText(getApplicationContext(), result.getText(), Toast.LENGTH_LONG).show();
                final String res = result.getText();
                dialogFragment = MyConfirmDialog.newInstance();
                if (res.startsWith("http:") || res.startsWith("https:")) {

                    dialogFragment.setOnOkClickListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(res));
                            startActivity(intent);
                            barcodeViewSingle();
                        }
                    });
                }

                dialogFragment.setOnNgClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        barcodeViewSingle();
                    }
                });
                dialogFragment.setOnNtClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openChooserToShareThisApp(res);
                    }
                });

                dialogFragment.setContent("結果", res);
                dialogFragment.show(getFragmentManager(), "dialog_fragment");
            }

            @Override
            public void possibleResultPoints(List resultPoints) {
            }
        });
    }


    /// このアプリをSNSシェアできるIntentを起動する
    private void openChooserToShareThisApp(String res) {
        ShareCompat.IntentBuilder builder
                = ShareCompat.IntentBuilder.from(this);
        String subject = "シンプルQR読み取り結果";
        String bodyText = res;
        builder.setSubject(subject) /// 件名
                .setText(bodyText)  /// 本文
                .setType("text/plain");
        Intent intent = builder.createChooserIntent();

        /// 結果を受け取るように起動
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUESTCODE_SNS_SHARE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}