package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private int index = 0;
    private static final int PERMISSION_CODES = 1234;
    private static final int CAPTURE_CODE = 1001;
    ArrayList<String> listImage, list_Image;
    Button add_url_btn, take_photo_btn;
    EditText inputURL;
    String URL;
    Uri image_uri;
    MyDatabase myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listImage = new ArrayList<String>();

        imageView = findViewById(R.id.rv_images);
        listImage.add("https://kynguyenlamdep.com/wp-content/uploads/2022/06/hinh-nen-doraemon-de-thuong.jpg") ;
        listImage.add("https://24hstore.vn/upload_images/images/SEO/AUDIT/hinh-nen-iphone-doc-dep.jpg") ;
        listImage.add("https://img4.thuthuatphanmem.vn/uploads/2020/07/04/anh-nen-cute-hoat-hinh-cho-dien-thoai_061912395.jpg") ;
        listImage.add("https://cdn.24h.com.vn/upload/1-2021/images/2021-02-26/image50-1614333620-651-width500height800.jpg") ;


        

        MyDatabase myDB = new MyDatabase(MainActivity.this);

        storeDataInArrays();

        loadImage();

        take_photo_btn = findViewById(R.id.take_photo_btn);
        add_url_btn = findViewById(R.id.addLink);
        inputURL = findViewById(R.id.linkInput);


        add_url_btn.setOnClickListener(v -> {

            URL = inputURL.getText().toString().trim();

//            boolean checkUrl = IsValidUrl(URL);

//            if(checkUrl){
                listImage.add(URL);
                Log.d("", Arrays.toString(listImage.toArray()));
                Log.d("", listImage.get(listImage.size()-1));

                Glide.with(MainActivity.this)
                        .load(listImage.get(listImage.size()-1))
                        .centerCrop()
                        .override(600, 1000)
                        .into(imageView);

                myDB.addURL(listImage.get(listImage.size()-1));

//            }
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

    }

    private void loadImage() {
        Glide.with(MainActivity.this)
                .load(listImage.get(index))
                .centerCrop()
                .override(600, 1000)
                .into(imageView);
    }

    public void nextImage(View view){
        index++;
        if(index >= listImage.size())
            index = 0;

        loadImage();
    }

    public void previousImage(View view){
        index--;
        if(index <= 1)
            index = listImage.size()-1;

        loadImage();
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
        if(resultCode == RESULT_OK){
            imageView.setImageURI(image_uri);
        }
    }

    private boolean IsValidUrl(String urlString) {

        String WebUrl = "^((ftp|http|https):\\/\\/)?(www.)?(?!.*(ftp|http|https|www.))[a-zA-Z0-9_-]+(\\.[a-zA-Z]+)+((\\/)[\\w#]+)*(\\/\\w+\\?[a-zA-Z0-9_]+=\\w+(&[a-zA-Z0-9_]+=\\w+)*)?$";

        if (urlString.length() > 0) {
            if (!urlString.matches(WebUrl)) {
                //validation msg
                inputURL.requestFocus();
                inputURL.setError("It isn't URL!");
                return false;
            }else{
                return true;
            }
        }else{
            inputURL.requestFocus();
            inputURL.setError("Fill cannot be empty!");
            return false;
        }
    }

    void storeDataInArrays() {
        MyDatabase myDB = new MyDatabase(MainActivity.this);
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(MainActivity.this, "No data", Toast.LENGTH_SHORT).show();
        }else{
            while(cursor.moveToNext()){
                list_Image = new ArrayList<>();
                list_Image.add(cursor.getString(1));
            }
        }
    }
}

