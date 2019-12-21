package com.rmit.cloudcomputing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.unstoppable.submitbuttonview.SubmitButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Activity_CreateProduct extends AppCompatActivity{
    private SubmitButton sBtnLoading;
    private ImageButton imageButton;
    private File photoFile = null;
    private EditText name;
    private EditText price;
    private AsyncTask<File, Integer, Response> task;
    private Button reset;
    private String from;
    private Double oriprice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageButton = findViewById(R.id.imageButton);
        setContentView(R.layout.activity_create_product);
        reset=findViewById(R.id.reset);

        ImageButton imageButton = findViewById(R.id.imageButton);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        from=getIntent().getStringExtra("from");
        oriprice=getIntent().getDoubleExtra("price",0);
        if(from.equals("edit")){
            Picasso.get()
                    .load(getIntent().getStringExtra("image"))
                    .placeholder(R.drawable.noimage)
                    .resize(100, 100)
                    .error(R.drawable.noimage)
                    .into(imageButton);
            name.setText(getIntent().getStringExtra("name"));
            price.setText(String.valueOf(oriprice));
        }
        //Init submit button
        sBtnLoading = findViewById(R.id.sbtn_loading);
        sBtnLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        if ((from.equals("create")&&photoFile == null) || name.getText().length() == 0 || price.getText().length() == 0) {
                            Toast.makeText(Activity_CreateProduct.this, "Incompeleted Product", Toast.LENGTH_SHORT).show();
                            sBtnLoading.reset();
                        } else {
                            task = new CreateProduct();
                            task.execute(photoFile);
                        }
            }
        });

        //Init reset button
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sBtnLoading.reset();
            }
        });

        //Set submit button result
        sBtnLoading.setOnResultEndListener(new SubmitButton.OnResultEndListener() {
            @Override
            public void onResultEnd() {
            }
        });

        //Handle image select
        final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu imagepop = new PopupMenu(Activity_CreateProduct.this, v);
                imagepop.getMenuInflater().inflate(R.menu.uploadimage, imagepop.getMenu());
                imagepop.show();
                imagepop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.album:
                                if (ContextCompat.checkSelfPermission(Activity_CreateProduct.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(Activity_CreateProduct.this, PERMISSIONS, 113);
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    intent.setType("image/*");
                                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
                                }
                                break;

                            case R.id.camera:
                                if (ContextCompat.checkSelfPermission(Activity_CreateProduct.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(Activity_CreateProduct.this, new String[]{Manifest.permission.CAMERA}, 114);
                                } else {
                                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                    try {
                                        photoFile = createImageFile();
                                    } catch (IOException ex) {

                                    }
                                    Uri photoURI = FileProvider.getUriForFile(Activity_CreateProduct.this,
                                            "com.rmit.cloudcomputing.fileprovider",
                                            photoFile);
                                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(cameraIntent, 2);
                                }
                        }
                        return false;
                    }
                });

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageButton = findViewById(R.id.imageButton);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                String imagepath = imageUri.getPath();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(imageUri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imagepath = cursor.getString(columnIndex);
                cursor.close();
                File imagefile = new File(imagepath);
                photoFile = imagefile;
                imageButton.setImageURI(data.getData());
            }

        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(photoFile.getPath());
            imageButton.setImageBitmap(imageBitmap);
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }


    class CreateProduct extends AsyncTask<File, Integer, Response> {

    @Override
    protected Response doInBackground(File... files) {
        String url = "https://cc20192.appspot.com/product";
        try {
            MultipartBody.Builder mutipartbuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("name", name.getText().toString());

            if(files[0]!=null){
                mutipartbuilder.addFormDataPart("image", "image", RequestBody.create(files[0], MediaType.parse("image/*png")));
            }
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();
            Request request;
            if(from.equals("create")) {
                mutipartbuilder.addFormDataPart("price", price.getText().toString());
                RequestBody requestBody = mutipartbuilder.build();
                request = new Request.Builder()
                        .url(url)
                        .header("ACTION","create")
                        .header("Content-Type", "multipart/form-data")
                        .url(url)
                        .post(requestBody)
                        .build();
            }
            else{
                if(price!=null&&!(oriprice.toString()).equals(price.getText().toString())){
                    mutipartbuilder.addFormDataPart("price", price.getText().toString());
                }
                mutipartbuilder.addFormDataPart("id",getIntent().getStringExtra("id"));
                RequestBody requestBody = mutipartbuilder.build();
                request = new Request.Builder()
                        .url(url)
                        .header("ACTION","update")
                        .header("Content-Type", "multipart/form-data")
                        .url(url)
                        .post(requestBody)
                        .build();
            }
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

        @Override
        protected void onPostExecute(Response result) {
        if(result==null){
            sBtnLoading.doResult(false);
            reset.setVisibility(View.VISIBLE);
            Toast.makeText(Activity_CreateProduct.this, "Request timeout, please try again!", Toast.LENGTH_SHORT).show();
        }
        else if(result.code()==200){
            try {
                reset.setVisibility(View.GONE);
                sBtnLoading.doResult(true);
                Toast.makeText(Activity_CreateProduct.this, "Succeed", Toast.LENGTH_SHORT).show();
                if (from.equals("edit")) {
                    JSONArray jsonArray = new JSONArray(result.body().string());
                    int position = getIntent().getIntExtra("position", 0);
                    DataHolder.products.get(position).setImage(jsonArray.getJSONObject(0).getString("imageURLs"));
                    DataHolder.products.get(position).setName(name.getText().toString());
                    DataHolder.products.get(position).setPrice(Double.valueOf(price.getText().toString()));
                    Activity_Result.adapter.notifyItemChanged(position);
                }
            }
            catch (JSONException | IOException e){
                e.printStackTrace();
            }
        }
        else{
            sBtnLoading.doResult(false);
            reset.setVisibility(View.VISIBLE);
            Toast.makeText(Activity_CreateProduct.this, result.message(), Toast.LENGTH_SHORT).show();
        }
        }
}
}
