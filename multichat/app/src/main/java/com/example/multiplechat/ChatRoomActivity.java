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
    //===일반 필드===
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

        //===채팅방 노드의 하위에 메시지를 저장 하기 위한 노드 선언===//
        root=firebaseDatabase.getReference();
        chatListNode=root.child("ChatList");
        chatRoom=chatListNode.child(getIntent().getStringExtra("chatRoomName"));
        msgNode=chatRoom.child("Message");

        chatListNode2=root.child("chatList2");
        chatNode2=chatListNode2.child(getIntent().getStringExtra("chatRoomName")); //현재 들어와 있는 대화방의 이름 노드를 가져온다.

        //msgAdapter객체를 생성하고 msgListLv에 할당한다.
        msgAdapter=new MessageAdapter(messages, getLayoutInflater(), firebaseAuth.getCurrentUser().getEmail());
        msgListView.setAdapter(msgAdapter);

        roomNameTv.setText(getIntent().getStringExtra("chatRoomName")); //화면 상단에 현재 보고 있는 대화방의 이름 표시

        //전송 버튼 선택 시 동작
        sendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(firebaseAuth.getCurrentUser().getEmail());
            }
        });//setOnClickListener()

        //우측 상단의 사용자 초대 버튼 선택 시 동작
        userInvitationBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),UserInvitationActivity.class).putExtra("chatRoomName",getIntent().getStringExtra("chatRoomName")));
            }
        });//setOnClickListener()

        //상단 좌측의 대화방 나가기 버튼 선택 시 동작
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

        //DB에 저장된 메시지들을 읽어와 ListView표시 하는 동작
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

    //전송 버튼 선택 시 사용자가 입력한 메시지를 DB에 저장하는 메소드
    private void sendMessage(String email){
        //메시지 입력 텍스트 창이 공란인지 검사한다.
        if(TextUtils.isEmpty(msgTv.getText())){
            Toast.makeText(getApplicationContext(),"전송할 메시지를 입력 하세요.",Toast.LENGTH_SHORT).show();
        }else{
            Calendar calendar = Calendar.getInstance();
            String date = calendar.get(Calendar.YEAR)+"."+(calendar.get(Calendar.MONTH)+1)+"."+calendar.get(Calendar.DAY_OF_MONTH);
            String time = (calendar.get(Calendar.HOUR_OF_DAY))+":" +calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);

            //Calendar객체를 통해 현재 날짜와 시간에 대한 정보를 메시지를 전송한 사용자 email과 함께 String 객체에 저장한다.
            String inputMessage=email+"!_#_!"+msgTv.getText().toString()+"!_#_!"+date+"!_#_!"+time;

            //message노드의 하위노드로 사용자가 전송한 메시지 값을 저장한다.
            msgNode.push().setValue(inputMessage);

            msgTv.setText("");
        }
    }//sendMessageToDB()

    //DB에서 메시지를 읽어오는 메소드
    private  void readMessage(DataSnapshot snapshot){
        String newMessage = snapshot.getValue().toString(); //DB로부터 message하위 노드의 데이터를 가져와 저장한다.
        StringTokenizer st=new StringTokenizer(newMessage,"!_#_!"); //가져온 메시지들을 토큰을 사용하여 구분한다.

        messages.add(new Message(st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken()));
        msgAdapter.notifyDataSetChanged();
    }//readMessage()

    //나가기 버튼 선택 시 동작하는 메소드, DB로부터 현재 들어와 있는 대화방의 노드에 대한 정보를 매개변수로 받는다.
    private void runExit(DataSnapshot snapshot){
        //DB로부터 현재 대화방에 소속되어있는 사용자들의 email정보를 가져온다.
        String tmp=snapshot.getValue().toString();
        StringTokenizer st=new StringTokenizer(tmp,"#");
        int z=st.countTokens();

        if(z<=1){
            //사용자가 대화방에서 퇴장하려 할 때 해당 사용자가 대화방에 남은 마지막 사용자라면 DB의 사용자 값에 null저장하여 해당 대화방을 제거한다.
            chatRoom.removeValue(); //사용자가 전송한 메시지를 보관하고있는 message노드 또한 삭제하여 해당 대화방과 관련한 메시지가 남아 있지 않게한다.
            chatListNode2.child(getIntent().getStringExtra("chatRoomName")).setValue(null);
        }else{
            tmp=tmp.replaceAll("#"+firebaseAuth.getCurrentUser().getEmail(),""); //해당 대화방에 소속 되어있는 사용자들의 목록에서 현재 로그인한 사용자의 email값을 지운다.
            chatListNode2.child(getIntent().getStringExtra("chatRoomName")).setValue(tmp); //현재 로그인한 사용자의 email을 지운 사용자 값을 다시 저장한다.
        }
        Toast.makeText(getApplicationContext(),snapshot.getKey()+"대화방 퇴장",Toast.LENGTH_LONG).show();
        finish(); //대화방에서 탈퇴 후 해당 화면을 종료한다.
    }
}