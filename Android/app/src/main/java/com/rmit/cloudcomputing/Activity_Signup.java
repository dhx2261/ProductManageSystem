package com.rmit.cloudcomputing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Activity_Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        final EditText signupusername=findViewById(R.id.signupusername);
        final EditText signuppassword=findViewById(R.id.signuppasswotd);
        final EditText confirmpass=findViewById(R.id.comfirmpassword);
        final Spinner spinner=findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.usertype, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Button submit=findViewById(R.id.signupsubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!signuppassword.getText().toString().equals(confirmpass.getText().toString())){
                    Toast.makeText(Activity_Signup.this, "Password not match", Toast.LENGTH_SHORT).show();
                }
                else if (signuppassword.getText().length()>0&&confirmpass.getText().length()>0&&signupusername.getText().length()>0){
                    AsyncTask_SignUp asyncTask_signUp=new AsyncTask_SignUp(Activity_Signup.this);
                    asyncTask_signUp.execute(signupusername.getText().toString(),signuppassword.getText().toString(),spinner.getSelectedItem().toString());
                }
            }
        });
    }
}
