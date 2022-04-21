package com.example.labskill2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.labskill2.sql.UserDBHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText usernameET;
    EditText passwordET;
    Button signInBtn;
    TextView regLinkTV;

    SharedPreferences loginPref;

    SharedPreferences.Editor editor;
    String actualUserName;
    String actualPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginPref = getSharedPreferences("login_details", MODE_PRIVATE);
        if(loginPref.contains("logout_status")){
            //the account already registered
            if (!(loginPref.getBoolean("logout_status",true))){
                Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                mainIntent.putExtra("usernameKey",loginPref.getString("username",""));
                startActivity(mainIntent);
                finish();
            }
        }
        else
            Toast.makeText(getApplicationContext(), "Need to create account",Toast.LENGTH_SHORT).show();

        usernameET = findViewById(R.id.usernameLoginEditText);
        passwordET = (EditText) findViewById(R.id.passwordLoginEditText);

        signInBtn = (Button) findViewById(R.id.loginButton);
        signInBtn.setOnClickListener(loginOnClickListener);

        regLinkTV = (TextView) findViewById(R.id.registerButtonTextView);
        regLinkTV.setOnClickListener(loginOnClickListener);

    }

    private View.OnClickListener loginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.loginButton:
                    if (verifyLogin())
                        finish();
                    break;
                case R.id.registerButtonTextView:
                    register();
                    break;
            }
        }
    };

    private boolean verifyLogin(){
        UserDBHandler userDBHandler = new UserDBHandler(this);

        String username = usernameET.getText().toString();
        ArrayList<HashMap<String, String>> userList = userDBHandler.GetUserByUsername(username);
        if (userList.isEmpty()==false) {
            actualPassword = userList.get(0).get("password");
            //Log.d("Test", "Password" + actualPassword);
            if (passwordET.getText().toString().equals(actualPassword)){
                editor = loginPref.edit();
                editor.clear();
                editor.putBoolean("logout_status", false);
                editor.putString("username", usernameET.getText().toString());
                editor.putInt("id", Integer.valueOf(userList.get(0).get("id")));
                editor.commit();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            }
        }
        Toast.makeText(this, "Wrong username or password", Toast.LENGTH_LONG).show();
        return false;
    }

    public void register(){
        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}