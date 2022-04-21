package com.example.labskill2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.labskill2.sql.UserDBHandler;
import com.example.labskill2.ui.setting.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.labskill2.databinding.ActivityMainBinding;

import java.io.File;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;


    SharedPreferences loginPref;
    UserDBHandler userDB;
    User user;
    final String TAG = "Main";
    TextView headerEmailTV;
    TextView headerUsernameTV;
    Boolean userExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = new User();
        userDB = new UserDBHandler(this);
        loginPref = getSharedPreferences("login_details", Context.MODE_PRIVATE);
        userExist = profileDataExist();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail(view);
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_calculategcpa)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout:
                goToLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToLogout() {
        SharedPreferences.Editor editor = loginPref.edit();
        editor.clear();
        editor.putBoolean("logout_status", true);
        editor.commit();
        Intent intent =  new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        // Every time Navigation is brought up
        setPhotoProfile();
        headerEmailTV = (TextView) findViewById(R.id.navHeaderEmailTV);
        headerUsernameTV = (TextView) findViewById(R.id.navHeaderUsernameTV);
        if (userExist) setProfileData();

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setPhotoProfile() {
        SharedPreferences pref = getSharedPreferences("image_path", Context.MODE_PRIVATE);
        if(pref.contains("profilePhotoPath")) {
            String currentPhotoPath = pref.getString("profilePhotoPath", "");
            if(currentPhotoPath.isEmpty() == false) {
                File imgFile = new File(currentPhotoPath);
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ImageView frontImgHead = (ImageView) findViewById(R.id.navHeaderImageView);
                frontImgHead.setImageBitmap(bitmap);
            }
        }
    }

    public boolean profileDataExist() {
        SharedPreferences loginPref = getSharedPreferences("login_details", Context.MODE_PRIVATE);
        if (loginPref.contains("logout_status")) {
            String username = loginPref.getString("username", "");
            int id = loginPref.getInt("id", -99);
            Log.d(TAG, username);
            if (username.isEmpty() == false) {
                ArrayList<HashMap<String, String>> userList = userDB.GetUserByUserId(id);

                if(userList.isEmpty() == false) {
                    user.setUsername(username);
                    user.setId(id);
                    user.setName(userList.get(0).get("name"));
                    user.setPhoneNo(userList.get(0).get("phoneNo"));
                    user.setEmail(userList.get(0).get("email"));
                    user.setPassword(userList.get(0).get("password"));
                    Log.d(TAG,  user.getEmail());
                    return true;
                }
            }
        }
        return false;
    }

    public void setProfileData(){
        headerUsernameTV.setText("Welcome, " + user.getUsername());
        headerEmailTV.setText(user.getEmail());
    }

    protected void sendEmail(View view) {
        Log.i("Send email", "");

        String[] TO = {user.getEmail()};
        String[] CC = {"kokeaston@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hi " + user.getName() + ", Welcome to my Lab Skill 2 Application");
        emailIntent.putExtra(Intent.EXTRA_TEXT,
                "Phone No: " + user.getPhoneNo()
                        + ", Email: " + user.getEmail()
                        + ", User Registration ID: " + user.getId()
                        + ", Your Username: " + user.getUsername()
                        + ", Don't forget your Password: " + user.getPassword());

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i(TAG, "Finished sending email...");
            Snackbar.make(view, "A Welcome Message has been sent to your Email Account!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Log.i(TAG, ex.toString());
            Snackbar.make(view, "There is no Email Application on this Client Device!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}