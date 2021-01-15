package com.itokikaku.qrcodereader3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

import androidx.appcompat.app.AppCompatActivity;
//https://blog.shg25.com/?p=184
//https://qiita.com/Mosea/items/e9dae626713fe9950734 // [Kotlin] Zxing QRコードリーダーをカスタマイズ
//http://9ensan.com/blog/smartphone/android/android-qr-zxing-sample/
//https://phoneappli.hatenablog.com/entry/2020/12/17/161115
//http://andante.in/i/qr%E3%82%B3%E3%83%BC%E3%83%89/qr%E3%82%B3%E3%83%BC%E3%83%89%E8%AA%AD%E3%81%BF%E8%BE%BC%E3%81%BF%E3%82%92%E7%B5%84%E3%81%BF%E8%BE%BC%E3%82%80/
//https://chibashi.me/development/android/android-app-kotlin-zxing-20200703/
//https://github.com/journeyapps/zxing-android-embedded
public class QrActivity extends AppCompatActivity {
    private static final String TAG = QrActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // EditTextにQRコードの内容をセット
                EditText et = (EditText) findViewById(R.id.et);
                et.setText(result.getContents());
                et.setSelection(et.getText().length());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_capture: // キャプチャ画面起動
                IntentIntegrator integrator = new IntentIntegrator(this);

                // Fragmentで呼び出す場合
                // IntentIntegrator integrator = IntentIntegrator.forFragment(this);

                // 独自でキャプチャ画面のActivityを作成
                // integrator.setCaptureActivity(ToolbarCaptureActivity.class);
                // → QrToolbarCaptureActivityのSample： https://github.com/journeyapps/zxing-android-embedded/blob/master/sample/src/main/java/example/zxing/ToolbarCaptureActivity.java

                // スキャンするバーコード形式を指定
                // integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);

                // キャプチャ画面の下方にメッセージを表示
                integrator.setPrompt("Scan a barcode");

                // カメラの特定（この場合はフロントカメラを使用）
                // integrator.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);

                // 読み取り時の音声をオフに
                integrator.setBeepEnabled(false);

                // バーコードを画像保存できるっぽい（保存先はonActivityResultでIntentResult#getBarcodeImagePath()で取得）
                integrator.setBarcodeImageEnabled(true);

                // スキャン画面の回転の制御
                integrator.setOrientationLocked(true);

                // キャプチャ画面起動
                integrator.initiateScan();


                break;
            case R.id.create_qr: // QRコード生成
                Bitmap bitmap;
                try {
                    EditText et = (EditText) findViewById(R.id.et);
                    String text = et.getText().toString();
                    if (text.equals("")) {
                        return;
                    }
                    bitmap = createQRCodeByZxing(et.getText().toString(), 480);
                    ImageView iv = (ImageView) findViewById(R.id.iv);
                    iv.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    Log.e(TAG, "WriterException", e);
                    return;
                }
                break;
        }
    }

    // ここそのまま→ http://qiita.com/alingogo/items/3006e5685057c23db6bd
    // ありがとうございます。
    public Bitmap createQRCodeByZxing(String contents, int size) throws WriterException {
        //QRコードをエンコードするクラス
        QRCodeWriter writer = new QRCodeWriter();

        //異なる型の値を入れるためgenericは使えない
        Hashtable encodeHint = new Hashtable();

        //日本語を扱うためにシフトJISを指定
        encodeHint.put(EncodeHintType.CHARACTER_SET, "shiftjis");

        //エラー修復レベルを指定
        //L 7%が復元可能
        //M 15%が復元可能
        //Q 25%が復元可能
        //H 30%が復元可能
        encodeHint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        BitMatrix qrCodeData = writer.encode(contents, BarcodeFormat.QR_CODE, size, size, encodeHint);

        //QRコードのbitmap画像を作成
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.argb(255, 255, 255, 255)); //いらないかも
        for (int x = 0; x < qrCodeData.getWidth(); x++) {
            for (int y = 0; y < qrCodeData.getHeight(); y++) {
                if (qrCodeData.get(x, y) == true) {
                    //0はBlack
                    bitmap.setPixel(x, y, Color.argb(255, 0, 0, 0));
                } else {
                    //-1はWhite
                    bitmap.setPixel(x, y, Color.argb(255, 255, 255, 255));
                }
            }
        }

        return bitmap;
    }
}