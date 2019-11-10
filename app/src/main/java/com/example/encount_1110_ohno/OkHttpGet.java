package com.example.encount_1110_ohno;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpGet extends AsyncTask<String,String,String> {
    @Override
    protected String doInBackground(String... strings) {

        // url
        String url = "https://kinako.cf/api/api.php?cpu=AMD%20CPU%20100-100000071BOX";
        // レスポンスデータ
        String strData = "";

        // OkHttpのインスタンス生成
        OkHttpClient client = new OkHttpClient();

        // urlを含めてリクエストをビルド
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            // リクエスト実行
            Response response = client.newCall(request).execute();
            // レスポンスのbodyからデータ取得
            strData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strData;
    }

    @Override
    protected void onPostExecute(String str) {
        Log.d("Debug",str);
    }
}
