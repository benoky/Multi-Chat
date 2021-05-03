package com.example.multiplechat;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {
    //===XML===
    private EditText emailTv, passwordTv;
    private Button loginBt, registerBt;
    //===firebase===
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        emailTv=(EditText)findViewById(R.id.etv_email);
        passwordTv=(EditText)findViewById(R.id.etv_password);
        loginBt=(Button)findViewById(R.id.bt_login);
        registerBt=(Button)findViewById(R.id.bt_register);

        firebaseAuth=FirebaseAuth.getInstance(); //파이어베이스 인증을 위한 객체 가져오기

        //로그인 버튼 선택 시 동작
        loginBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                runLogin();
            }
        });//setOnClickListener()

        //회원가입 버튼 선택 시 동작
        registerBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                goRegister();
            }
        });//setOnClickListener()
    }//onCreate();

    private void runLogin(){
        //firebase에 해당 문자열들이 일치하는지 검사 요청
        firebaseAuth.signInWithEmailAndPassword(emailTv.getText().toString(),passwordTv.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) { //task에 로그인의 성공 여부가저장 되어 있음
                    //로그인이 성공적으로 실행 되면 MainActivity를 호출하고 현재 화면을 종료한다.
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                } else {
                    emailTv.setText("");
                    passwordTv.setText("");
                    Toast.makeText(getApplicationContext(),"다시 입력해 주세요.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }//runLogin()

    private void goRegister(){
        //RegisterActivity화면을 호출한다.
        startActivity(new Intent(this, RegisterActivity.class));
    }//goRegister()
}