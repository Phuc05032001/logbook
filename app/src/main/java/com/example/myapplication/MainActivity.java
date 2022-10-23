package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

// import gói thư viện java.util.ArrayList
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> listImage;
    Button add_url_btn;
    EditText inputURL;
    String URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listImage = new ArrayList<String>();

        add_url_btn = findViewById(R.id.addLink);
        inputURL = findViewById(R.id.linkInput);

        add_url_btn.setOnClickListener(v -> {

            URL = inputURL.getText().toString().trim();

            boolean checkUrl = IsValidUrl(URL);

            if(checkUrl){
                listImage.add(URL);
                Log.d("", Arrays.toString(listImage.toArray()));
                Log.d("", listImage.get(listImage.size()-1));
                MyDatabase myDB = new MyDatabase(MainActivity.this);
                myDB.addURL(listImage.get(listImage.size()-1));
            }
        });
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
}

