package com.rmit.cloudcomputing;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AsyncTask_Delete extends AsyncTask<String,String, Response> {
    private Context activity;
    private ProgressDialog pd;
    private Adapter_ResultList adapter_resultList;
    private int position;

    public AsyncTask_Delete(Context activity, Adapter_ResultList adapter_resultList, int position) {
        this.activity = activity;
        this.adapter_resultList = adapter_resultList;
        this.position = position;
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
        String url="https://cc20192.appspot.com/product?id="+strings[0];
        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .delete()
                    .build();
            return client.newCall(request).execute();
        }

        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response result) {
        if (pd.isShowing()) {
            pd.dismiss();
        }
        try {
            JSONObject jsonObject=new JSONObject(result.body().string());
            if (result.code() == 200) {
                Toast nomatch = Toast.makeText(activity, "Delete product succeed", Toast.LENGTH_SHORT);
                nomatch.show();
                DataHolder.products.remove(position);
                adapter_resultList.notifyItemRemoved(position);
                adapter_resultList.notifyItemRangeChanged(position, DataHolder.products.size());
            }
            else if(result.code()==400){
                Toast.makeText(activity, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
            }

        }
        catch (IOException | JSONException | NullPointerException e){
            Toast.makeText(activity, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
        }
    }


}
