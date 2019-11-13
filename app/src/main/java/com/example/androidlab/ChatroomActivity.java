package com.example.androidlab;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatroomActivity extends AppCompatActivity {

    public static ArrayList<Message> msgList = new ArrayList<>();

    BaseAdapter adapter;
    Button receiverBtn;
    Button senderBtn;
    EditText msgInput;
    Cursor results;
    SQLiteDatabase db;
    Message msg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        ListView chatListView = findViewById(R.id.chatListView);

        MyDatabaseOpenHelper dbOpener = new MyDatabaseOpenHelper(this);
        db = dbOpener.getWritableDatabase();
        String[] columns = {MyDatabaseOpenHelper.COL_ID,MyDatabaseOpenHelper.COL_ISSEND,MyDatabaseOpenHelper.COL_MESSAGE};
        results = db.query
                (false,MyDatabaseOpenHelper.TABLE_NAME,columns,null, null, null, null, null, null);

        int messageColumnIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_MESSAGE);
        int indexColumnIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_ID);
        int isSendIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_ISSEND);

        while(results.moveToNext()){
            int isSend = results.getInt(isSendIndex);
            String msgContent = results.getString(messageColumnIndex);
            Long msgId = results.getLong(indexColumnIndex);
            msg = new Message(msgContent,isSend,msgId);
            msgList.add(msg);
            printCusor(results);
        }

        adapter = new MyAdapter();
        chatListView.setAdapter(adapter);
//
        receiverBtn = findViewById(R.id.receiverBtn);
        receiverBtn.setOnClickListener(clk ->{
            msgInput = findViewById(R.id.msgInput);
            String content = msgInput.getText().toString();
            ContentValues newRowValues = new ContentValues();
            newRowValues.put(MyDatabaseOpenHelper.COL_MESSAGE, content);
            newRowValues.put(MyDatabaseOpenHelper.COL_ISSEND, Message.TYPE_RECEIVE);
            long id = db.insert(MyDatabaseOpenHelper.TABLE_NAME,null,newRowValues);
            msg = new Message(content,Message.TYPE_RECEIVE,id);
            msgList.add(msg);
            msgInput.setText("");
            adapter.notifyDataSetChanged();
        });

        senderBtn = findViewById(R.id.sendBtn);
        senderBtn.setOnClickListener(clk->{
            msgInput = findViewById(R.id.msgInput);
            String content = msgInput.getText().toString();
            ContentValues newRowValues = new ContentValues();
            newRowValues.put(MyDatabaseOpenHelper.COL_MESSAGE, content);
            newRowValues.put(MyDatabaseOpenHelper.COL_ISSEND, Message.TYPE_SEND);
            long id = db.insert(MyDatabaseOpenHelper.TABLE_NAME,null,newRowValues);
            Message msg = new Message(content,Message.TYPE_SEND,id);
            msgList.add(msg);
            msgInput.setText("");
            adapter.notifyDataSetChanged();
        });
    }

    private void printCusor(Cursor c){
        //The database version number

        Log.e("The version is", db.getVersion()+";" );
        //The number of columns in the cursor.
        Log.e("Total columns: ", c.getColumnCount()+ ";" );
        //The name of the columns in the cursor.
        Log.e("Name of the columns: ",  Arrays.toString(c.getColumnNames())+";" );
        //	The number of results in the cursor
        Log.e("Total results: ", c.getCount()+ ";" );
        //Each row of results in the cursor.

    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return msgList.size();
        }

        @Override
        public Message getItem(int position) {
            return msgList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View theRow = null;
            if(theRow == null){
                Message msg = getItem(position);
                if(msg.sendOrReceive == Message.TYPE_SEND){
                    theRow = inflater.inflate(R.layout.sender_layout,null);
                    TextView senderContent = theRow.findViewById(R.id.senderContent);
                    senderContent.setText(msg.msg);
                }
                if(msg.sendOrReceive==Message.TYPE_RECEIVE){
                    theRow = inflater.inflate(R.layout.receiver_layout,null);
                    TextView receiverContent = theRow.findViewById(R.id.receiverContent);
                    receiverContent.setText(msg.msg);
                }
            }
            return theRow;
        }
    }
}
