package com.example.multiplechat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.multiplechat.message.Message;
import com.example.multiplechat.message.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class ChatRoomActivity extends AppCompatActivity {
    //===XML===
    private EditText msgTv;
    private TextView roomNameTv;
    private Button sendBt;
    private ListView msgListView;
    private ImageButton userInvitationBt;
    private ImageButton exitBt;
    //===firebase===
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    //DatabaseReference===
    private DatabaseReference root;
    private DatabaseReference chatListNode;
    private DatabaseReference chatRoom;
    private DatabaseReference msgNode;
    private DatabaseReference chatListNode2;
    private DatabaseReference chatNode2;
    //===μΌλ° νλ===
    private ArrayList<Message> messages=new ArrayList<>();
    private MessageAdapter msgAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatroom_layout);

        msgTv=(EditText)findViewById(R.id.etv_msg);
        roomNameTv=(TextView)findViewById(R.id.tv_roomname);
        sendBt=(Button)findViewById(R.id.bt_send);
        msgListView=(ListView)findViewById(R.id.lv_msgview);
        userInvitationBt=(ImageButton)findViewById(R.id.bt_invitation);
        exitBt=(ImageButton)findViewById(R.id.bt_exit);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

        //===μ±νλ°© λΈλμ νμμ λ©μμ§λ₯Ό μ μ₯ νκΈ° μν λΈλ μ μΈ===//
        root=firebaseDatabase.getReference();
        chatListNode=root.child("ChatList");
        chatRoom=chatListNode.child(getIntent().getStringExtra("chatRoomName"));
        msgNode=chatRoom.child("Message");

        chatListNode2=root.child("chatList2");
        chatNode2=chatListNode2.child(getIntent().getStringExtra("chatRoomName")); //νμ¬ λ€μ΄μ μλ λνλ°©μ μ΄λ¦ λΈλλ₯Ό κ°μ Έμ¨λ€.

        //msgAdapterκ°μ²΄λ₯Ό μμ±νκ³  msgListLvμ ν λΉνλ€.
        msgAdapter=new MessageAdapter(messages, getLayoutInflater(), firebaseAuth.getCurrentUser().getEmail());
        msgListView.setAdapter(msgAdapter);

        roomNameTv.setText(getIntent().getStringExtra("chatRoomName")); //νλ©΄ μλ¨μ νμ¬ λ³΄κ³  μλ λνλ°©μ μ΄λ¦ νμ

        //μ μ‘ λ²νΌ μ ν μ λμ
        sendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(firebaseAuth.getCurrentUser().getEmail());
            }
        });//setOnClickListener()

        //μ°μΈ‘ μλ¨μ μ¬μ©μ μ΄λ λ²νΌ μ ν μ λμ
        userInvitationBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),UserInvitationActivity.class).putExtra("chatRoomName",getIntent().getStringExtra("chatRoomName")));
            }
        });//setOnClickListener()

        //μλ¨ μ’μΈ‘μ λνλ°© λκ°κΈ° λ²νΌ μ ν μ λμ
        exitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatNode2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        runExit(snapshot);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });//addValueEventListener()
            }
        });//setOnClickListener()

        //DBμ μ μ₯λ λ©μμ§λ€μ μ½μ΄μ ListViewνμ νλ λμ
        msgNode.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                readMessage(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });//addChildEventListener()
    }//onCreate()

    //μ μ‘ λ²νΌ μ ν μ μ¬μ©μκ° μλ ₯ν λ©μμ§λ₯Ό DBμ μ μ₯νλ λ©μλ
    private void sendMessage(String email){
        //λ©μμ§ μλ ₯ νμ€νΈ μ°½μ΄ κ³΅λμΈμ§ κ²μ¬νλ€.
        if(TextUtils.isEmpty(msgTv.getText())){
            Toast.makeText(getApplicationContext(),"μ μ‘ν  λ©μμ§λ₯Ό μλ ₯ νμΈμ.",Toast.LENGTH_SHORT).show();
        }else{
            Calendar calendar = Calendar.getInstance();
            String date = calendar.get(Calendar.YEAR)+"."+(calendar.get(Calendar.MONTH)+1)+"."+calendar.get(Calendar.DAY_OF_MONTH);
            String time = (calendar.get(Calendar.HOUR_OF_DAY))+":" +calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);

            //Calendarκ°μ²΄λ₯Ό ν΅ν΄ νμ¬ λ μ§μ μκ°μ λν μ λ³΄λ₯Ό λ©μμ§λ₯Ό μ μ‘ν μ¬μ©μ emailκ³Ό ν¨κ» String κ°μ²΄μ μ μ₯νλ€.
            String inputMessage=email+"!_#_!"+msgTv.getText().toString()+"!_#_!"+date+"!_#_!"+time;

            //messageλΈλμ νμλΈλλ‘ μ¬μ©μκ° μ μ‘ν λ©μμ§ κ°μ μ μ₯νλ€.
            msgNode.push().setValue(inputMessage);

            msgTv.setText("");
        }
    }//sendMessageToDB()

    //DBμμ λ©μμ§λ₯Ό μ½μ΄μ€λ λ©μλ
    private  void readMessage(DataSnapshot snapshot){
        String newMessage = snapshot.getValue().toString(); //DBλ‘λΆν° messageνμ λΈλμ λ°μ΄ν°λ₯Ό κ°μ Έμ μ μ₯νλ€.
        StringTokenizer st=new StringTokenizer(newMessage,"!_#_!"); //κ°μ Έμ¨ λ©μμ§λ€μ ν ν°μ μ¬μ©νμ¬ κ΅¬λΆνλ€.

        messages.add(new Message(st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken()));
        msgAdapter.notifyDataSetChanged();
    }//readMessage()

    //λκ°κΈ° λ²νΌ μ ν μ λμνλ λ©μλ, DBλ‘λΆν° νμ¬ λ€μ΄μ μλ λνλ°©μ λΈλμ λν μ λ³΄λ₯Ό λ§€κ°λ³μλ‘ λ°λλ€.
    private void runExit(DataSnapshot snapshot){
        //DBλ‘λΆν° νμ¬ λνλ°©μ μμλμ΄μλ μ¬μ©μλ€μ emailμ λ³΄λ₯Ό κ°μ Έμ¨λ€.
        String tmp=snapshot.getValue().toString();
        StringTokenizer st=new StringTokenizer(tmp,"#");
        int z=st.countTokens();

        if(z<=1){
            //μ¬μ©μκ° λνλ°©μμ ν΄μ₯νλ € ν  λ ν΄λΉ μ¬μ©μκ° λνλ°©μ λ¨μ λ§μ§λ§ μ¬μ©μλΌλ©΄ DBμ μ¬μ©μ κ°μ nullμ μ₯νμ¬ ν΄λΉ λνλ°©μ μ κ±°νλ€.
            chatRoom.removeValue(); //μ¬μ©μκ° μ μ‘ν λ©μμ§λ₯Ό λ³΄κ΄νκ³ μλ messageλΈλ λν μ­μ νμ¬ ν΄λΉ λνλ°©κ³Ό κ΄λ ¨ν λ©μμ§κ° λ¨μ μμ§ μκ²νλ€.
            chatListNode2.child(getIntent().getStringExtra("chatRoomName")).setValue(null);
        }else{
            tmp=tmp.replaceAll("#"+firebaseAuth.getCurrentUser().getEmail(),""); //ν΄λΉ λνλ°©μ μμ λμ΄μλ μ¬μ©μλ€μ λͺ©λ‘μμ νμ¬ λ‘κ·ΈμΈν μ¬μ©μμ emailκ°μ μ§μ΄λ€.
            chatListNode2.child(getIntent().getStringExtra("chatRoomName")).setValue(tmp); //νμ¬ λ‘κ·ΈμΈν μ¬μ©μμ emailμ μ§μ΄ μ¬μ©μ κ°μ λ€μ μ μ₯νλ€.
        }
        Toast.makeText(getApplicationContext(),snapshot.getKey()+"λνλ°© ν΄μ₯",Toast.LENGTH_LONG).show();
        finish(); //λνλ°©μμ νν΄ ν ν΄λΉ νλ©΄μ μ’λ£νλ€.
    }
}