package com.example.encount_1110_ohno;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * POST通信(コメント投稿)する時に利用するクラス
 * OkHttp3を使用
 */

public class OkHttpPost extends AsyncTask<String,String,String> {


    //public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //jsonのサンプルデータ
    //String json = "{\"name\":\"名前\", \"taxis\":\"分類\"}";

    //写真のパスを受け取る変数(将来的には撮影した写真のパス、ファイル名を取得して指定する)
    public static String uurl = "";

    //user-id(将来的にはAndroid内のSQLiteから取得)
    public String id = String.valueOf(2);;
    //緯度
    public static String latitude = "35.703092";
    //経度
    public static String longitude = "139.985561";
    //送信するコメント内容の受け取り変数
    public static String cmnt = "";

    //写真の保存先デバッグ用
    public static Uri basyo = null;

    @Override
    protected String doInBackground(String... strings) {

        System.out.println("場所は："+basyo);

        //アクセスするURL
        //String url = "https://kinako.cf/api/pass_check.php";
        //String url = "https://kinako.cf/api/upload.php";
        //String url = "https://kinako.cf/encount/upload.php";
        String url = "https://kinako.cf/encount/PostPhoto.php";


            //Map<String, String> formParamMap = new HashMap<>();
            //formParamMap.put("word", "abc");

        //写真のパスを取得する
        //File file = new File(ImagePath[0]);
        File file = new File(uurl);

        //ファイルの存在確認(uurlの写真が存在するのか)　※デバッグ用
        if(file.exists()){
            System.out.println("ファイルが存在します。");
        }else{
            System.out.println("ファイルが存在しません。");
        }
        //デバッグ用
        System.out.println(file);


        /**
         * ここから旧処理
         */

        //OkHttpClient client = new OkHttpClient();
        //Formを作成
        /*final FormBody.Builder formBuilder = new FormBody.Builder();

            //formParamMap.forEach(formBuilder::add);

        //formに要素を追加
        formBuilder.add("id", id);
        formBuilder.add("latitude", latitude);
        formBuilder.add("longitude", longitude);
        formBuilder.add("word", cmnt);
        //リクエストの内容にformを追加
        RequestBody body = formBuilder.build();

            //RequestBody body = RequestBody.create(JSON, json);

        //リクエストを生成
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();*/
        /**
         * 旧処理ここまで
         */

        //ここでPOSTする内容を設定　"image/jpg"の部分は送りたいファイルの形式に合わせて変更する
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId",id)
                .addFormDataPart("longitude",longitude)
                .addFormDataPart("latitude",latitude)
                .addFormDataPart("word",cmnt)
                .addFormDataPart(
                        "file",
                        file.getName(),
                        RequestBody.create(MediaType.parse("image/jpg"), file))
                .build();

        //タイムアウトの設定
        //デフォルトのままだとタイムアウトしてしまうので、少し大きい値を設定している
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .connectTimeout(10,TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();

        //リクエストの作成
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        //ここまで

        try {
            Response response = client.newCall(request).execute();
            //System.out.println(response.body().string());
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String str) {
        //結果をログに出力(レスポンスのbodyタグ内を出力する)
        Log.d("Debug",str);
    }
}