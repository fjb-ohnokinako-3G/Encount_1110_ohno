package com.example.encount_1110_ohno;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * POST通信(投稿)する時に利用するクラス
 * OkHttp3を使用
 */

public class OkHttpPost extends AsyncTask<String,String,String> {


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //jsonのサンプルデータ
    //String json = "{\"name\":\"名前\", \"taxis\":\"分類\"}";

    //送信するコメント内容の受け取り変数
    public static String cmnt = "";

    @Override
    protected String doInBackground(String... strings) {

        OkHttpClient client = new OkHttpClient();

        //アクセスするURL
        String url = "https://kinako.cf/api/pass_check.php";

            //Map<String, String> formParamMap = new HashMap<>();
            //formParamMap.put("word", "abc");

        //Formを作成
        final FormBody.Builder formBuilder = new FormBody.Builder();

            //formParamMap.forEach(formBuilder::add);

        //formに要素を追加
        formBuilder.add("word", cmnt);
        //リクエストの内容にformを追加
        RequestBody body = formBuilder.build();

            //RequestBody body = RequestBody.create(JSON, json);

        //リクエストを生成
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
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