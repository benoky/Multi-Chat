package com.example.multiplechat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    //===XML===
    private EditText newEmailTv, newPasswordTv;
    private Button completeBt;
    //===firebase===
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    //===DatabaseReference ===
    private DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        newEmailTv=(EditText)findViewById(R.id.etv_newemail);
        newPasswordTv=(EditText)findViewById(R.id.etv_newpassword);
        completeBt=(Button)findViewById(R.id.bt_complete);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

        root=firebaseDatabase.getReference();

        //완료 버튼 선택 시 동작
        completeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runRegister();
            }
        });//setOnClickListener()
    }//onCreate()

    private void runRegister(){
        //사용자가 입력한 계정 정보를 firebase에 전송한다.
        firebaseAuth.createUserWithEmailAndPassword(newEmailTv.getText().toString(),newPasswordTv.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                //계정 정보가 성공적으로 등록 되었는지 판단
                if(task.isSuccessful()){
                    //회원 가입이 완료 되면 Email값을 userList노드의 하위노드에 값을 저장한다.
                    root.child("userList").push().setValue(newEmailTv.getText().toString());
                    Toast.makeText(getApplicationContext(),"회원 가입 성공",Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"이메일 또는 비밀번호를 확인해주세요.",Toast.LENGTH_LONG).show();
                }
            }
        });//createUserWithEmailAndPassword()
    }//runRegister()
}
