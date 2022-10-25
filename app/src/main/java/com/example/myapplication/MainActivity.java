package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private int index = 0;
    private static final int PERMISSION_CODES = 1234;
    private static final int CAPTURE_CODE = 1001;
    ArrayList<String> listImage;
    Button add_url_btn, take_photo_btn, next, previous;
    EditText inputURL;
    String URL;
    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.rv_images);
        take_photo_btn = findViewById(R.id.take_photo_btn);
        add_url_btn = findViewById(R.id.addLink);
        inputURL = findViewById(R.id.linkInput);
        next = findViewById(R.id.forward_btn);
        previous = findViewById(R.id.backward_btn);

        listImage = new ArrayList<String>();

        next.setOnClickListener(v -> {
            index++;
            if(index == listImage.size()){
                index = 0;
            }

            loadImage();
        });

        previous.setOnClickListener(v -> {
            index--;
            if(index < 0){
                index = listImage.size()-1;
            }
            loadImage();
        });

        add_url_btn.setOnClickListener(v -> {

            URL = inputURL.getText().toString().trim();

            boolean checkUrl = IsValidUrl(URL);

            if(checkUrl){
                listImage.add(URL);
//                Log.d("", Arrays.toString(listImage.toArray()));
//                Log.d("", listImage.get(listImage.size()-1));
                Glide.with(MainActivity.this)
                        .load(listImage.get(listImage.size()-1))
                        .centerCrop()
                        .into(imageView);
                MyDatabase myDB = new MyDatabase(MainActivity.this);
                myDB.addURL(listImage.get(listImage.size()-1));

            }
        });

        take_photo_btn.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                    String[] permission =  {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, PERMISSION_CODES);

                }else{
                    openCamera();
                }
            } else{
               openCamera();
            }
        });

        storeDataInArrays();

        loadImage();

    }

    private void loadImage() {
        if (listImage.size()<=0){
            Glide.with(MainActivity.this)
                    .load("https://www.generationsforpeace.org/wp-content/uploads/2018/03/empty-300x240.jpg")
                    .centerCrop()
                    .into(imageView);
        }

        Glide.with(MainActivity.this)
                .load(listImage.get(index))
                .centerCrop()
                .into(imageView);
    }

    private void openCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"new image");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent Cam_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Cam_intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(Cam_intent, CAPTURE_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_CODES:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }
                else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imageView.setImageURI(image_uri);
        }
    }


    private boolean IsValidUrl(String URL) {

        String FileExtension = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";

        if (URL.length() > 0){
            if(URLUtil.isNetworkUrl(URL)){
                if(!URL.matches(FileExtension)){
                    inputURL.requestFocus();
                    inputURL.setError("It isn't image URL!");
                }else{
                    Toast.makeText(MainActivity.this, "OK!", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        }
        inputURL.requestFocus();
        inputURL.setError("It isn't image URL!");
        return false;
    }

    void storeDataInArrays() {
        MyDatabase myDB = new MyDatabase(MainActivity.this);
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(MainActivity.this, "No data", Toast.LENGTH_SHORT).show();
        }else{
            while(cursor.moveToNext()){
                listImage.add(cursor.getString(1));
            }
        }
    }
}

