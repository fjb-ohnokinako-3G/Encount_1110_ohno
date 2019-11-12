package com.example.encount_1110_ohno

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
//import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.Date

/**
 * カメラ機能
 */
class MainActivity : AppCompatActivity() {

    /**
     * 保存された画像のURI
     */
    private var _imageUri: Uri? = null

    /**
     * 緯度フィールド
     */
    private var _latitude = 0.0
    /**
     * 経度フィールド
     */
    private var _longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * 投稿処理
         * 投稿ボタン押すと動作する
         */
        // GETボタンとPOSTボタン取得
        val getButton = findViewById<Button>(R.id.getButton)
        val postButton = findViewById<Button>(R.id.postButton)
        val commentInput = findViewById<EditText>(R.id.commentInput)

        //投稿ボタン
        val postUpload = findViewById<Button>(R.id.postUpload)

        // GETボタンがタップされた時
        getButton.setOnClickListener(View.OnClickListener {
            val getTask = OkHttpGet()
            getTask.execute()
        })

        // POSTボタンが押された時
        postButton.setOnClickListener(View.OnClickListener {

            OkHttpPost.cmnt = commentInput.getText().toString()

            val postTask = OkHttpPost()
            postTask.execute()

        })

        // 投稿ボタンが押された時 画像
        postUpload.setOnClickListener(View.OnClickListener {

            //OkHttpPost.cmnt = commentInput.getText().toString()
            //var pass = "/storage/emulated/0/Pictures/1573437349657.jpg";
            //var pass = "content://com.android.externalstorage.documents/document/primary%3APictures%2F1573437349657.jpg";
            var pass = "/sdcard/Pictures/1573437349657.jpg";
            val postTask = PostImg()
            postTask.execute(pass)

        })
        //ここまで

        /**
         * 位置情報取得
         */
        //LocationManagerオブジェクトを取得。
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //位置情報が更新された際のリスナオブジェクトを生成。
        val locationListener = GPSLocationListener()
        //ACCESS_FINE_LOCATIONの許可が下りていないなら…
        if(ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //ACCESS_FINE_LOCATIONの許可を求めるダイアログを表示。その際、リクエストコードを1000に設定。
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this@MainActivity, permissions, 1000)
            //onCreate()メソッドを終了。
            return
        }
        //位置情報の追跡を開始。
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        //ここまで

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //カメラアプリからの戻りでかつ撮影成功の場合
        if(requestCode == 200 && resultCode == RESULT_OK) {
            //撮影された画像のビットマップデータを取得。
            //val bitmap = data?.getParcelableExtra<Bitmap>("data")
            //画像を表示するImageViewを取得。
            val ivCamera = findViewById<ImageView>(R.id.ivCamera)
            //撮影された画像をImageViewに設定。
            //ivCamera.setImageBitmap(bitmap)
            //フィールドの画像URIをImageViewに設定。
            ivCamera.setImageURI(_imageUri)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        //追加
        //ACCESS_FINE_LOCATIONに対するパーミションダイアログでかつ許可を選択したなら…
        if(requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //LocationManagerオブジェクトを取得。
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            //位置情報が更新された際のリスナオブジェクトを生成。
            val locationListener = GPSLocationListener()
            //再度ACCESS_FINE_LOCATIONの許可が下りていないかどうかのチェックをし、降りていないなら処理を中止。
            if(ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            //位置情報の追跡を開始。
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        }
        //ここまで

        //WRITE_EXTERNAL_STORAGEに対するパーミションダイアログでかつ許可を選択したなら…
        if(requestCode == 2000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //もう一度カメラアプリを起動。
            val ivCamera = findViewById<ImageView>(R.id.ivCamera)
            onCameraImageClick(ivCamera)
        }
    }

    /**
     * 画像部分がタップされたときの処理メソッド。
     * 1101 カメラのパーミッション設定の確認と同時に、ここで現在地取得のパーミッションも確認して、許可がないなら再度リクエストする処理を追加する
     */
    fun onCameraImageClick(view: View) {
        //WRITE_EXTERNAL_STORAGEの許可が下りていないなら…
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //WRITE_EXTERNAL_STORAGEの許可を求めるダイアログを表示。その際、リクエストコードを2000に設定。
            val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions, 2000)
            return
        }

        //日時データを「yyyyMMddHHmmss」の形式に整形するフォーマッタを生成。
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")
        //現在の日時を取得。
        val now = Date()
        //取得した日時データを「yyyyMMddHHmmss」形式に整形した文字列を生成。
        val nowStr = dateFormat.format(now)
        //ストレージに格納する画像のファイル名を生成。ファイル名の一意を確保するためにタイムスタンプの値を利用。
        val fileName = "UseCameraActivityPhoto_${nowStr}.jpg"

        /**
         * 画面要素にファイル名を表示するための変数
         */
        val ffn = findViewById<TextView>(R.id.tvWeatherDesc)
        tvWeatherDesc.text = fileName

        //ContentValuesオブジェクトを生成。
        val values = ContentValues()
        //画像ファイル名を設定。
        values.put(MediaStore.Images.Media.TITLE, fileName)
        //画像ファイルの種類を設定。
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

        //ContentResolverを使ってURIオブジェクトを生成。
        _imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //Intentオブジェクトを生成。
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //Extra情報として_imageUriを設定。
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageUri)
        //アクティビティを起動。
        startActivityForResult(intent, 200)
    }

    /**
     * ロケーションリスナクラス。
     */
    private inner class GPSLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            //引数のLocationオブジェクトから緯度を取得。
            _latitude = location.latitude
            //引数のLocationオブジェクトから経度を取得。
            _longitude = location.longitude
            //取得した緯度をTextViewに表示。
            val tvLatitude = findViewById<TextView>(R.id.tvLatitude)
            tvLatitude.text = _latitude.toString()
            //取得した経度をTextViewに表示。
            val tvLongitude = findViewById<TextView>(R.id.tvLongitude)
            tvLongitude.text = _longitude.toString()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
    }


}
