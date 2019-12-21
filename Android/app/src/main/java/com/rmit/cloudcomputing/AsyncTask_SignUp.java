package com.rmit.cloudcomputing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

public class AsyncTask_SignUp extends AsyncTask<String,Void,Response> {
    private Context context;
    ProgressDialog pd;
    private String usertype;

    public AsyncTask_SignUp(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(context);
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected Response doInBackground(String... strings) {
        String url="https://cc20192.appspot.com/user/register";
        usertype=strings[2];
        try{
            OkHttpClient client = new OkHttpClient();
            JSONObject jsonbody=new JSONObject();
            jsonbody.put("username",strings[0]);
            jsonbody.put("password",strings[1]);
            jsonbody.put("type",strings[2]);
            RequestBody body = RequestBody.create(jsonbody.toString(), MediaType.parse("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            return client.newCall(request).execute();
        }
        catch (IOException | JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response result) {
        pd.dismiss();
        if(result==null){
            Toast.makeText(context, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(result.body().string());
            if (result.code() == 200) {
                DataHolder.usertype = usertype;
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                ((Activity) context).finish();
            } else if (result.code() == 400) {
                Toast.makeText(context, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException | IOException e){
            Toast.makeText(context, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
