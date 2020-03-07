package com.example.androidlab;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    public static ArrayList<Message> msgList ;

    BaseAdapter adapter;
    Button receiverBtn;
    Button senderBtn;
    EditText msgInput;
    Cursor results;
    SQLiteDatabase db;
    Message msg;
    boolean isTablet;

    public static final int MSG_DETAIL = 345;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        msgList = new ArrayList<>();
        ListView chatListView = findViewById(R.id.chatListView);

        isTablet = findViewById(R.id.detailFrame) != null; //check if the FrameLayout is loaded

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

        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatroomActivity.this);
                builder.setMessage("Do you want to delete this message").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // What to do on Accept
                        Message message = (Message) adapter.getItem(position);
                        db.delete(MyDatabaseOpenHelper.TABLE_NAME,MyDatabaseOpenHelper.COL_ID+"=?",new String[]{message.msgid+""});
                        msgList.remove(message);
                        adapter.notifyDataSetChanged();
                    }
                }) .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // What to do on Cancel
                    }
                });
                builder.show();
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MSG_DETAIL)
        {
            if(resultCode == RESULT_OK) //if you hit the delete button instead of back button
            {
                int deletedItemPosition = data.getIntExtra("ItemPosition",-1);
                if(deletedItemPosition<0||deletedItemPosition>=msgList.size()){
                    Log.i("Delete Item","No Item Deleted");
                }else {
                    deleteMessageId(deletedItemPosition);
                }
            }
        }
    }

    public void deleteMessageId(int position)
    {
        Log.i("Delete this message:" , " position="+position);
        msgList.remove(position);
        adapter.notifyDataSetChanged();
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
            Intent goToMessageDetail = new Intent(ChatroomActivity.this,MessageDetail.class);
            if(theRow == null){
                Message msg = getItem(position);
                msg.setPosition(position);
                if(msg.sendOrReceive == Message.TYPE_SEND){
                    theRow = inflater.inflate(R.layout.sender_layout,null);
                    TextView senderContent = theRow.findViewById(R.id.senderContent);
                    senderContent.setText(msg.msg);
                    theRow.findViewById(R.id.senderIcon).setOnClickListener(clk->{
                        if(isTablet){
                            Bundle dataToPass = new Bundle();
                            dataToPass.putString("message_content", msg.msg );
                            dataToPass.putInt("message_position", msg.position);
                            dataToPass.putLong("message_id", msg.msgid);
                            dataToPass.putString("message_type","isSend");

                            DetailFragment dFragment = new DetailFragment(); //add a DetailFragment
                            dFragment.setArguments( dataToPass ); //pass it a bundle for information
                            dFragment.setTablet(isTablet);  //tell the fragment if it's running on a tablet or not
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.detailFrame, dFragment) //Add the fragment in FrameLayout
                                    .addToBackStack("AnyName") //make the back button undo the transaction
                                    .commit(); //actually load the fragment.
                        }else {
                            goToMessageDetail.putExtra("message", msg.msg);
                            goToMessageDetail.putExtra("sendOrReceive", "isSend");
                            goToMessageDetail.putExtra("msgId", msg.msgid);
                            goToMessageDetail.putExtra("position", msg.position);
                            startActivityForResult(goToMessageDetail, MSG_DETAIL);
                        }
                    });
                }
                if(msg.sendOrReceive==Message.TYPE_RECEIVE){
                    theRow = inflater.inflate(R.layout.receiver_layout,null);
                    TextView receiverContent = theRow.findViewById(R.id.receiverContent);
                    receiverContent.setText(msg.msg);
                    theRow.findViewById(R.id.receiverIcon).setOnClickListener(clk->{
                        if(isTablet){

                            Bundle dataToPass = new Bundle();
                            dataToPass.putString("message_content", msg.msg );
                            dataToPass.putInt("message_position", msg.position);
                            dataToPass.putLong("message_id", msg.msgid);
                            dataToPass.putString("message_type","isReceive");

                            DetailFragment dFragment = new DetailFragment(); //add a DetailFragment
                            dFragment.setArguments( dataToPass ); //pass it a bundle for information
                            dFragment.setTablet(isTablet);  //tell the fragment if it's running on a tablet or not
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.detailFrame, dFragment) //Add the fragment in FrameLayout
                                    .addToBackStack("AnyName") //make the back button undo the transaction
                                    .commit(); //actually load the fragment.
                        }else {

                            goToMessageDetail.putExtra("message", msg.msg);
                            goToMessageDetail.putExtra("sendOrReceive", "isReceive");
                            goToMessageDetail.putExtra("msgId", msg.msgid);
                            goToMessageDetail.putExtra("position", msg.position);
                            startActivityForResult(goToMessageDetail, MSG_DETAIL);
                        }
                    });
                }
            }
            return theRow;
        }
    }
}
