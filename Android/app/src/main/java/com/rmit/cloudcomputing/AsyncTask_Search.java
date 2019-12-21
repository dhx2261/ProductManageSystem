package com.rmit.cloudcomputing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AsyncTask_Search extends AsyncTask<String,Void,Response> {

    private Context context;
    private ProgressDialog pd;

    public AsyncTask_Search(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(context);
        pd.setMessage("Please wait");
        pd.show();
        DataHolder.products.clear();
    }

    @Override
    protected Response doInBackground(String... strings) {
        String url=strings[0];
        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            return client.newCall(request).execute();
        } catch (IOException e) {
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
        if(result==null){
            Toast.makeText(context, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            if (result.code() == 200) {
//                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                JSONArray jsonArray = new JSONArray(result.body().string());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject productjson = jsonArray.getJSONObject(i);
                    String id = productjson.getString("ProdID");
                    String image = productjson.getString("imageURLs");
                    double price = productjson.getDouble("prices_amountMin");
                    String name = productjson.getString("name");
                    Model_Product product = new Model_Product(id, name, price, image);
                    DataHolder.products.add(product);
                }
                Intent intent = new Intent(context, Activity_Result.class);
                context.startActivity(intent);
            }
            else if(result.code()==400){
                JSONObject jsonObject=new JSONObject(result.body().string());
                Toast.makeText(context, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
            }
        }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
    }

}
