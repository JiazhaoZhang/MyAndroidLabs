package com.example.androidlab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DetailFragment extends Fragment {

    private boolean isTablet;
    private Bundle dataFromActivity;
    private long msg_id;
    private int msg_position;
    private String msg_type;

    public void setTablet(boolean tablet) { isTablet = tablet; }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataFromActivity = getArguments();
        msg_id = dataFromActivity.getLong("message_id" );
        msg_position =dataFromActivity.getInt("message_position",-1);
        msg_type = dataFromActivity.getString("message_type");
        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.fragment_layout, container, false);

        //show the message
        TextView message = (TextView)result.findViewById(R.id.msgContentInFragment);
        message.setText(dataFromActivity.getString("message_content"));

        //show the id:
        TextView idView = (TextView)result.findViewById(R.id.msgIdInFragment);
        idView.setText("ID=" + msg_id);

        //show msg type
        TextView typeView = result.findViewById(R.id.SendOrReceiveInFragment);
        typeView.setText(msg_type);

        // get the delete button, and add a click listener:
        Button deleteButton = (Button)result.findViewById(R.id.deleteBtnInFragment);
        deleteButton.setOnClickListener( clk -> {

            if(isTablet) { //both the list and details are on the screen:
                ChatroomActivity parent = (ChatroomActivity)getActivity();
                if(msg_position>=0){
                    boolean isDeleted = parent.db.delete
                            (MyDatabaseOpenHelper.TABLE_NAME,MyDatabaseOpenHelper.COL_ID+"=?",new String[]{msg_id+""})>0;
                    if(isDeleted) {
                        parent.deleteMessageId(msg_position); //this deletes the item and updates the list
                    }
                }
                //now remove the fragment since you deleted it from the database:
                // this is the object to be removed, so remove(this):
                parent.getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
            //for Phone:
//            else //You are only looking at the details, you need to go back to the previous list page
//            {
//                EmptyActivity parent = (EmptyActivity) getActivity();
//                Intent backToFragmentExample = new Intent();
//                backToFragmentExample.putExtra(FragmentExample.ITEM_ID, dataFromActivity.getLong(FragmentExample.ITEM_ID ));
//
//                parent.setResult(Activity.RESULT_OK, backToFragmentExample); //send data back to FragmentExample in onActivityResult()
//                parent.finish(); //go back
//            }
        });
        return result;
    }
}
