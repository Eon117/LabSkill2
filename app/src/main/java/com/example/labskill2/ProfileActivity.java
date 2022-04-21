package com.example.labskill2;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.labskill2.sql.UserDBHandler;
import com.example.labskill2.ui.setting.User;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    EditText nameET;
    EditText mobileET;
    EditText emailET;
    EditText usernameET;
    EditText passwordET;
    Button addImgBtn;
    ImageButton pwdImgBtn;

    User user;
    UserDBHandler userDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameET = (EditText) findViewById(R.id.nameProfileET);
        mobileET = (EditText) findViewById(R.id.mobileProfileEditText);
        setDisplayPhoneNumber();
        emailET = (EditText) findViewById(R.id.emailProfileEditText);
        usernameET = (EditText) findViewById(R.id.usernameProfileET);
        passwordET = (EditText) findViewById(R.id.passwordProfileET);

        addImgBtn = (Button) findViewById(R.id.addProfileImgBtn);
        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyData())
                    addUserToDatabase();
            }
        });

        ImageButton showImgBtn = (ImageButton) findViewById(R.id.showPwdBtn);
        showImgBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    passwordET.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    Log.d(TAG, "onTouch: ACTION DOWN");
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    Log.d(TAG, "onTouch: ACTION UP");
                    passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                return false;
            }


        });

    }

    private boolean verifyData(){
        if(TextUtils.isEmpty(usernameET.getText().toString())) {
            usernameET.setError("The username cannot be empty.");
            return false;
        }

        if(TextUtils.isEmpty(passwordET.getText().toString())) {
            passwordET.setError("The password cannot be empty.");
            return false;
        }

        String emailToText = emailET.getText().toString();
        // validate email address ref: https://www.geeksforgeeks.org/implement-email-validator-in-android/
        if (!(!emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches())){
            emailET.setError("The email must be valid and cannot be empty.");
            return false;
        }

        return true;
    }

    private void setDisplayPhoneNumber(){
        if (ActivityCompat.checkSelfPermission(this, READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) ==
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tMgr = (TelephonyManager)   this.getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();
            mobileET.setText(mPhoneNumber);
            return;
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, 100);
        }
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, READ_SMS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                String mPhoneNumber = tMgr.getLine1Number();
                mobileET.setText(mPhoneNumber);
                break;
        }
    }

    public void addUserToDatabase(){
        user = new User();
        user.setName(nameET.getText().toString());
        user.setPhoneNo(mobileET.getText().toString());
        user.setEmail(emailET.getText().toString());
        user.setUsername(usernameET.getText().toString());
        user.setPassword(passwordET.getText().toString());

        userDBHandler = new UserDBHandler(this);

        String username = usernameET.getText().toString();
        ArrayList<HashMap<String, String>> userList = userDBHandler.GetUserByUsername(username);
        if (userList.isEmpty()) {
            userDBHandler.addUser(user);
            Toast.makeText(this, "User account successfully created.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            Toast.makeText(this, "Username " + username +" already exist", Toast.LENGTH_LONG).show();
            usernameET.setText("");
        }
    }

}