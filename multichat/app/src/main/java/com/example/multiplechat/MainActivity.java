package com.example.multiplechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.multiplechat.roomname.RoomName;
import com.example.multiplechat.roomname.RoomNameAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    //===XML===
    private ImageButton newChatBt;
    private ListView roomListLv;
    private TextView currentUserTv;
    //===firebase===
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    //DatabaseReference===
    private DatabaseReference root;
    private DatabaseReference chatListNode2;
    //===일반 필드===
    private ArrayList<RoomName> roomNames=new ArrayList<>();
    private RoomNameAdapter roomNameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        
        newChatBt=(ImageButton)findViewById(R.id.bt_newchat);
        roomListLv=(ListView)findViewById(R.id.lv_chatlist);
        currentUserTv=(TextView)findViewById(R.id.tv_currentuser);

        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        //===어플리케이션에 회원가입 할 경우 노드에 해당 유저의 이메일 등록, 이후 해당 유저가 들어 가 있는 채팅방들이 표시된다.===//
        root=firebaseDatabase.getReference();
        chatListNode2=root.child("chatList2");

        //우측 상단의 대화방 생성 버튼 선택 시 동작
        newChatBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //새로운 대화방을 생성하는 화면인 NewChatActivity를 호출한다.
                startActivity(new Intent(getApplicationContext(),NewChatActivity.class));
            }
        });//setOnClickListener()

        currentUserTv.setText("사용자 : "+firebaseAuth.getCurrentUser().getEmail()); //현재 접속한 사용자의 이메일을 표시힌다.

        //roomNameAdapter객체를 생성하고 roomListLv에 할당한다.
        roomNameAdapter=new RoomNameAdapter(roomNames, getLayoutInflater());
        roomListLv.setAdapter(roomNameAdapter);

        //현재 로그인한 사용자가 소속되어 있는 대화방의 목록들을 불러오는 동작
        chatListNode2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readRoomList(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });//addValueEventListener()

        //ListView에서 항목을 선택할 경우 동작
        roomListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //해당 항목에 나타나 있는 대화방의 이름을 Intent를 통해 ChatRoomActivity에 넘겨준다.
                startActivity(new Intent(getApplicationContext(),ChatRoomActivity.class).putExtra("chatRoomName",roomNames.get(position).getRoomName()));
            }
        });
    }//onCreate()

    //현재 로그인한 사용자가 소속되어 있는 대화방의 목록들을 불러오는 메소드
    private void readRoomList(DataSnapshot snapshot){
        roomNames.clear(); //ListView에 중복된 데이터가 표시되는걸 방지하기 위해 roomNames를 초기화 한다.
        String tmp;
        StringTokenizer st;
        //chatList2의 하위노드에 있는 대화방 목록에 값으로 저장되어있는 사용자email을 불러와 현재 로그인한 사용자가 속해 있는지 비교한다.
        for(DataSnapshot ds:snapshot.getChildren()){
           st=new StringTokenizer(ds.getValue().toString(),"#");
           int z=st.countTokens();
           for(int i=0;i<=z;i++){
               if(!(st.countTokens()==0)){
                   tmp=st.nextToken();
                   //해당 사용자가 현재 불러온 대화방에 속해 있다면 해당 대화방의 이름을 ListView에 표시한다.
                   if(firebaseAuth.getCurrentUser().getEmail().equals(tmp)){
                       roomNames.add(new RoomName(ds.getKey()));
                       roomNameAdapter.notifyDataSetChanged();
                       break; //사용자를 찾았다면 내부 반복문을 빠져나가 외부 반복문을 진행시킨다.
                   }//if
               }
           }//for
        }
    }//readRoomList()
}