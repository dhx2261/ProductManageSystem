package com.rmit.cloudcomputing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Activity_Login extends AppCompatActivity {
    private EditText usernameview;
    private EditText passwordview;
    private Button login;
    private Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameview=findViewById(R.id.username);
        passwordview=findViewById(R.id.password);
        login=findViewById(R.id.login);
        signup=findViewById(R.id.signup);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameview.getText().length()==0||passwordview.getText().length()==0){
                    Toast nomatch = Toast.makeText(Activity_Login.this, "Please enter your user name and password", Toast.LENGTH_SHORT);
                    nomatch.show();
                }
                else{
                    AsyncTask_Login asyncTask_login=new AsyncTask_Login(Activity_Login.this);
                    asyncTask_login.execute(usernameview.getText().toString(),passwordview.getText().toString());
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Activity_Login.this,Activity_Signup.class);
                startActivity(intent);
            }
        });
    }
}
