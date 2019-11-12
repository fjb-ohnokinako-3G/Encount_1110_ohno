package com.example.encount_1110_ohno;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static okhttp3.MultipartBody.FORM;

public class PostImg extends AsyncTask<String, String, String> {

    //送信するコメント内容の受け取り変数
    //public static String cmnt = "";
    //写真のパスを受け取る変数
    public static String uurl = "";

    @Override
    //POSTするファイルのパスを引数として貰っている
    protected String doInBackground(String... ImagePath) {
        //ポスト先のURL
        String url = "https://kinako.cf/upimg/upimg.php";

        //File file = new File(ImagePath[0]);
        File file = new File(uurl);

        // ファイルの存在確認
        if(file.exists()){
            System.out.println("ファイルが存在します。");
        }else{
            System.out.println("ファイルが存在しません。");
        }

        //デバッグ用
        System.out.println(file);

        //ここでPOSTする内容を設定　"image/jpg"の部分は送りたいファイルの形式に合わせて変更する
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "file",
                        file.getName(),
                        RequestBody.create(MediaType.parse("image/jpg"), file))
                .build();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        String result="";
        try {
            /*Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            {
                result = response.body().string();

                Log.d("Debug", result);*/

                Response response = client.newCall(request).execute();
                return response.body().string();
           // }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        //return result;
    }

    @Override
    protected void onPostExecute(String str) {
        //結果をログに出力(レスポンスのbodyタグ内を出力する)
        Log.d("Debug", str);
    }
}