package com.example.multiplechat;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.multiplechat.userList.UserList;
import com.example.multiplechat.userList.UserListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserInvitationActivity extends AppCompatActivity {
    //===XML===
    private ListView userListLv;
    //===firebase===
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    //DatabaseReference===
    private DatabaseReference root;
    private DatabaseReference userListNode;
    private DatabaseReference chatListNode2;
    private DatabaseReference chatNameNode;
    //===일반 필드===
    private ArrayList<UserList> userLists=new ArrayList<>();
    private UserListAdapter userListAdapter;
    private StringBuilder currentUserList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invation_layout);

        userListLv=(ListView)findViewById(R.id.lv_userlist);

        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        root=firebaseDatabase.getReference();
        userListNode=root.child("userList");
        chatListNode2=root.child("chatList2");
        chatNameNode=chatListNode2.child(getIntent().getStringExtra("chatRoomName"));

        userListAdapter=new UserListAdapter(userLists,getLayoutInflater());
        userListLv.setAdapter(userListAdapter);

        currentUserList=new StringBuilder();
        //현재 채팅방에 소속 되어 있는 유저들의 정보를 가져온다.
        chatNameNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserList.append(snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });//addListenerForSingleValueEvent()

        //해당 어플리케이션에 회원가입이 되어있는 유저들의 이메일들을 ListView에 나타낸다.
        userListNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userListRead(snapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });//addValueEventListener()

        //ListView의 항목을 선택 할 경우 이벤트 발생
        userListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                runInvitation(position);
            }
        });//setOnItemClickListener()
    }//onCreate();

    //해당 어플리케이션에 회원가입이 되어있는 유저들의 이메일들을 ListView에 나타내는 메소드
    private void userListRead(DataSnapshot snapshot){
        userLists.clear(); //ListView에 중복된 데이터가 표시되는걸 방지하기 위해 userLists를 초기화 한다.
        //userList노드에 등록 되어있는 사용자들의 이메일을 가져온다.
        for(DataSnapshot ds:snapshot.getChildren()) {
            //현재 로그인한 사용자는 ListView에 표시되지 않게 검사한다.
            if (!(firebaseAuth.getCurrentUser().getEmail().equals(ds.getValue().toString()))) {
                userLists.add(new UserList(ds.getValue().toString()));
            }
        }
        userListAdapter.notifyDataSetChanged();
    }//userListRead()

    //ListView에서 각 항목을 선택 할 경우 해당 사용자를 대화방에 포함 시키는 메소드
    private void runInvitation(int i){
        //해당 항목의 번호를 매개변수로 받아 userLists로부터 해당 유저의 email을 받아와 사용자 목록에 포함시킨다.
        currentUserList.append("#"+userLists.get(i).getUserName());
        chatListNode2.child(getIntent().getStringExtra("chatRoomName")).setValue(currentUserList.toString()); //새로운 사용자 email이 포함된 사용자 목록을 다시 DB에 저장한 후
        //해당 화면을 종료한다.
        finish();
    }//runInvitation()
}
