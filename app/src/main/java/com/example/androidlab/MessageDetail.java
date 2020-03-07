package com.example.androidlab;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.DatabaseMetaData;

public class MessageDetail extends AppCompatActivity {

    private long msgId;
    private String msgContent;
    private String sendOrReceive;
    private int position;
    private TextView msgContentView;
    private TextView msgIdView;
    private TextView typeView;
    private Button deleteBtn;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        Intent datafromChatRoom = getIntent();
        msgContent = datafromChatRoom.getStringExtra("message");
        msgId = datafromChatRoom.getLongExtra("msgId",-1);
        sendOrReceive = datafromChatRoom.getStringExtra("sendOrReceive");
        msgContentView = findViewById(R.id.msgHere);
        msgContentView.setText(msgContent);
        msgIdView = findViewById(R.id.msgId);
        msgIdView.setText(msgId+"");
        typeView = findViewById(R.id.SendOrReceive);
        typeView.setText(sendOrReceive);

        position = datafromChatRoom.getIntExtra("position",-1);

        deleteBtn = findViewById(R.id.deleteMsg);
        deleteBtn.setOnClickListener(clk->{
            //Toast.makeText(this,"delete button",Toast.LENGTH_LONG).show();
            MyDatabaseOpenHelper dbOpener = new MyDatabaseOpenHelper(this);
            db = dbOpener.getWritableDatabase();
            boolean isDeleted = db.delete
                    (MyDatabaseOpenHelper.TABLE_NAME,MyDatabaseOpenHelper.COL_ID+"=?",new String[]{msgId+""})>0;
            if(isDeleted){
                Toast.makeText(this,"Message deleted",Toast.LENGTH_LONG).show();
                Intent backToChatRoom = new Intent();
                backToChatRoom.putExtra("ItemPosition", position);

                setResult(Activity.RESULT_OK, backToChatRoom); //send data back to FragmentExample in onActivityResult()
                finish(); //go back

            }else{
                Toast.makeText(this,"Message not deleted",Toast.LENGTH_LONG).show();
            }
        });
    }
}
