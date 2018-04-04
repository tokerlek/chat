package com.example.ogulcantoker.anniversarychat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PlayGamesAuthCredential;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = new String();
    private static int SIGN_IN_REQUEST_CODE = 1;
    //private FirebaseListAdapter<AnniversaryChat> adapter;
    RelativeLayout activity_main;
    ImageView send;
    DatabaseReference mDatabase;
    RecyclerView mMessageList;
    MyAdapter adapter;
    List<AnniversaryChat> listData;
    FirebaseDatabase FDB;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out){
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_main,"You have been signed out.", Snackbar.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Snackbar.make(activity_main,"Successfully signed in.Welcome!",Snackbar.LENGTH_SHORT).show();

            }
            else{
                Snackbar.make(activity_main,"Please try again later",Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


     /*   mMessageList = (RecyclerView) findViewById(R.id.list_of_messages);
        mMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mMessageList.setLayoutManager(linearLayoutManager);
     */
        mMessageList = (RecyclerView) findViewById(R.id.list_of_messages);
        mMessageList.setHasFixedSize(true);
        RecyclerView.LayoutManager LM = new LinearLayoutManager(getApplicationContext());
        mMessageList.setLayoutManager(LM);
        mMessageList.setItemAnimator(new DefaultItemAnimator());
        mMessageList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        listData = new ArrayList<>();

        adapter = new MyAdapter(listData);

        FDB = FirebaseDatabase.getInstance();

        GetDataFirebase();

        activity_main = (RelativeLayout)findViewById(R.id.activity_main);
        send = (ImageView)findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText)findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference().push().setValue(new AnniversaryChat(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");
                mMessageList.scrollToPosition(listData.size()-1);

            }
        });

        //Giris yapmadiysa giris sayfasina yonlendirme
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGN_IN_REQUEST_CODE);
        }
        else{
            Snackbar.make(activity_main,"Welcome "+FirebaseAuth.getInstance().getCurrentUser().getEmail(),Snackbar.LENGTH_SHORT).show();
            //Load content
        }

    }

    void GetDataFirebase(){
        mDatabase = FDB.getReference();
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                AnniversaryChat data = dataSnapshot.getValue(AnniversaryChat.class);
              /*  data.setMessageText(dataSnapshot.getValue().toString());
               // data.setMessageUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
               // data.setMessageTime(data.getMessageTime());*/
                listData.add(data);
                mMessageList.setAdapter(adapter);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                AnniversaryChat data = dataSnapshot.getValue(AnniversaryChat.class);
              //  data.setMessageText(dataSnapshot.getValue().toString());
                // data.setMessageUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                listData.remove(data);
                mMessageList.setAdapter(adapter);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
         super.onStart();
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MessageViewHolder>{

        List<AnniversaryChat> listArray;

        public MyAdapter(List<AnniversaryChat> List){
            this.listArray = List;
        }

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
            return new  MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
            AnniversaryChat data = listArray.get(position);

            if(data.getMessageUser().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                holder.MyText.setTextColor(Color.RED);
                holder.MyUser.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            }
            else{
                holder.MyText.setTextColor(Color.BLUE);
                holder.MyUser.setText(data.getMessageUser());
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification.Builder builder = new Notification.Builder(MainActivity.this);
                builder.setContentTitle("2. Yılımız Kutlu Olsun");
                builder.setContentText("aylavyu");
                builder.setSmallIcon(R.drawable.ic_send_message);
                builder.setAutoCancel(true);
                builder.setTicker("kehkeh");

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,1,intent,0);
                builder.setContentIntent(pendingIntent);

                Notification notification = builder.getNotification();
                manager.notify(1,notification);
            }
            holder.MyText.setText(data.getMessageText());
            holder.MyTime.setText(DateFormat.format("HH:mm" ,data.getMessageTime()));

        }

        public int getItemCount(){
            return listArray.size();
        }
        public class MessageViewHolder extends RecyclerView.ViewHolder{
            TextView MyText;
            TextView MyUser;
            TextView MyTime;
            public MessageViewHolder(View itemView) {
                super(itemView);
                MyText = (TextView) itemView.findViewById(R.id.message_text);
                MyUser = (TextView) itemView.findViewById(R.id.message_user);
                MyTime = (TextView) itemView.findViewById(R.id.message_time);
            }
        }
    }







}
