package com.example.multiplechat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class  NewChatActivity extends AppCompatActivity {
    //===XML===
    private EditText newChatNameTv;
    private Button createBt;
    //===firebase===
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    //DatabaseReference===
    private DatabaseReference root;
    private DatabaseReference chatListNode;
    private DatabaseReference chatNode;
    private DatabaseReference chatListNode2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newchat_layout);

        newChatNameTv=(EditText)findViewById(R.id.etv_newchatname);
        createBt=(Button)findViewById(R.id.bt_create);

        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        //생성 버튼 선택 시 동작작
       createBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runCreate();
            }
        });
    }//onCreate()

    private void runCreate(){
        //사용자가 입력한 대화방 제목이 2~15글자 이내인지 검사한다.
        if(isLength(newChatNameTv.getText().toString())){
            root=firebaseDatabase.getReference(); //DB에서 Root노드의 정보를 가져온다
            chatListNode=root.child("ChatList"); //Root노드의 하위 노드로 대화방 목록을 가지는 ChatList노드를 생성한다
            chatNode=chatListNode.child(newChatNameTv.getText().toString()); //ChatList의 하위노드로 사용자가 입력한 대화방의 이름으로 노드를 생성한다

            chatListNode2=root.child("chatList2");
            //현재 생성한 대화방의 이름을 chatList2의 하위노드로 넣고 그 값을 대화방을 생성한 사용자의 이메일을 넣는다
            chatListNode2.child(newChatNameTv.getText().toString()).setValue("#"+firebaseAuth.getCurrentUser().getEmail());

            Toast.makeText(getApplicationContext(),"대화방 생성 완료",Toast.LENGTH_LONG).show();
            //대화방이 생성되면 해당 대화방의 화면인 ChatRoomActivity를 호출하고 해당 화면은 종료한다.
            startActivity(new Intent(this,ChatRoomActivity.class).putExtra("chatRoomName",newChatNameTv.getText().toString()));
            finish();
        }else{
            Toast.makeText(getApplicationContext(),"다시 확인해 주세요.",Toast.LENGTH_LONG).show();
        }
    }//runCreate()

    //사용자가 입력한 대화방 이름이 조건에 부합하는지 검사하는 메소드
    private boolean isLength(String chatName){
        if(chatName.length()<2){
            return false;
        }else if(chatName.length()>15){
            return false;
        }else{
            return true;
        }
    }//isLength()
}
