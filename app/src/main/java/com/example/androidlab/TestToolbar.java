package com.example.androidlab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class TestToolbar extends AppCompatActivity {

    Toolbar toolbar;
    String myMsg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_toolbar);
        toolbar = findViewById(R.id.testToolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.item1:
                Toast.makeText(getApplicationContext(),"This is the initial message",Toast.LENGTH_LONG).show();
                break;
            case R.id.item2:
                LayoutInflater myinflater = getLayoutInflater();
                View v = myinflater.inflate(R.layout.dialogue_layout, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Input Your Message").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        // What to do on Accept
                            Toast.makeText(getApplicationContext(),"CLick Yes",Toast.LENGTH_LONG).show();
                            EditText editText = v.findViewById(R.id.dia_msg);
                            myMsg = editText.getText().toString();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        // What to do on Cancel
                            Toast.makeText(getApplicationContext(),"CLick No",Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }
                    });
                builder.setView(v);
                builder.create().show();
                break;
            case R.id.item3:
                Snackbar sbar = Snackbar.make(toolbar,"Wanna go back?",Snackbar.LENGTH_INDEFINITE);
                sbar.setAction("Back",e->finish()).show();
                break;
            case R.id.item4:
                Toast.makeText(this,myMsg,Toast.LENGTH_LONG).show();
                break;
        }

        return true;
    }
}