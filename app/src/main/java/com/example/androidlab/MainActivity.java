package com.example.androidlab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = (EditText) findViewById(R.id.inputEmail);

        //use a Lambda function to set a click listener
        Button page1Button = (Button) findViewById(R.id.loginButton);
        if (page1Button != null) {
            page1Button.setOnClickListener(clk -> {
                Intent goToPage2 = new Intent(MainActivity.this, SecondActivity.class);
                goToPage2.putExtra("emailInput", editText.getText().toString());

                startActivity(goToPage2);
                // startActivityForResult(goToPage2, 30);
            });
        }
    }
}
