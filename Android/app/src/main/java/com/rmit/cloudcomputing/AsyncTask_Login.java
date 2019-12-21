package com.rmit.cloudcomputing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AsyncTask_Login extends AsyncTask<String ,String, Response> {
    private Activity activity;
    private ProgressDialog pd;

    public AsyncTask_Login(Activity activity) {
        this.activity = activity;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(activity);
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected Response doInBackground(String... strings) {
        String url="https://cc20192.appspot.com/user/login";
        try {
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonbody=new JSONObject();
        jsonbody.put("username",strings[0]);
        jsonbody.put("password",strings[1]);
        RequestBody body = RequestBody.create(jsonbody.toString(), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

            return client.newCall(request).execute();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response result) {
        if (pd.isShowing()){
            pd.dismiss();
        }
        try {
        if(result==null){
            Toast nomatch = Toast.makeText(activity, "Something went wrong, please try again.", Toast.LENGTH_SHORT);
            nomatch.show();
        }
        else if(result.code()==200){
                JSONObject jsonObject=new JSONObject(result.body().string());
                DataHolder.usertype=jsonObject.getString("type");
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
        }
        else {
            JSONObject jsonObject=new JSONObject(result.body().string());
            Toast nomatch = Toast.makeText(activity, jsonObject.getString("error"), Toast.LENGTH_SHORT);
            nomatch.show();
        }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
