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

public class OkHttpPost extends AsyncTask<String,String,String> {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //String json = "{\"name\":\"名前\", \"taxis\":\"分類\"}";

    public static String cmnt = "コメントです";

    @Override
    protected String doInBackground(String... strings) {

        OkHttpClient client = new OkHttpClient();

        String url = "https://kinako.cf/api/pass_check.php";

        String comecome = SubClass.comecome;

        //Map<String, String> formParamMap = new HashMap<>();
        //formParamMap.put("word", "abc");
        final FormBody.Builder formBuilder = new FormBody.Builder();
        //formParamMap.forEach(formBuilder::add);
        formBuilder.add("word", comecome);
        RequestBody body = formBuilder.build();

        //RequestBody body = RequestBody.create(JSON, json);

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
        Log.d("Debug",str);
    }
}