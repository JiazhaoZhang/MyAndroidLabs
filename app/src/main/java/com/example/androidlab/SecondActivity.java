package com.example.androidlab;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageButton camara;
    TextView emailInput;
    private int requestCode;
    private int resultCode;
    @Nullable
    private Intent data;
    ImageButton mImageButton;
    Button toChat;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent dataFromPreviousPage = getIntent();

        String whatUserTyped = dataFromPreviousPage.getStringExtra("emailInput");
        emailInput = (TextView) findViewById(R.id.p2emailInput);
        emailInput.setText(whatUserTyped);
        mImageButton = findViewById(R.id.pictureButton);
        mImageButton.setOnClickListener((view) ->{dispatchTakePictureIntent();} );

        toChat = findViewById(R.id.toChat);
        toChat.setOnClickListener((view)->{
            Intent goToChat = new Intent(SecondActivity.this,ChatroomActivity.class);
            startActivity(goToChat);
        });

        Button toWeater = findViewById(R.id.toWeather);
        if(toWeater != null){
            toWeater.setOnClickListener((view)->{
                Intent goToWeather = new Intent(SecondActivity.this,WeatherForecast.class);
                startActivity(goToWeather);
            });
        }
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageButton.setImageBitmap(imageBitmap);
        }
    }
}

