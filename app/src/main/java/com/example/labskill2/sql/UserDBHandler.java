package com.example.labskill2.sql;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.labskill2.ui.setting.User;

import java.util.ArrayList;
import java.util.HashMap;

public class UserDBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "LabSkill2DB.db";

    // User table name
    private static final String TABLE_USER = "user";

    // User Table Columns names
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_PHONENO = "phone_no";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_USERNAME = "username";
    private static final String COLUMN_USER_PASSWORD = "password";

    public UserDBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create table sql query
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_USER_PHONENO + " TEXT," + COLUMN_USER_EMAIL + " TEXT,"
                + COLUMN_USER_USERNAME + " TEXT," + COLUMN_USER_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        // Create tables again
        onCreate(db);
    }

    // **** CRUD (Add, Read, Update, Delete) Operations ***** //

    /**
     * This method is to create user record
     */
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_PHONENO,user.getPhoneNo());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_USERNAME, user.getUsername());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    // Get User id and password based on username
    public ArrayList<HashMap<String, String>>  GetUserByUsername(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_PASSWORD
        };

        // selection criteria
        String selection = COLUMN_USER_USERNAME + " = ?" ;

        // selection arguments
        String[] selectionArgs = {username};
        // query user table with conditions
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        if (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("id",cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.put("password",cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)));
            userList.add(user);
        }
        return  userList;
    }

    // Get User Details based on userid
    public ArrayList<HashMap<String, String>> GetUserByUserId(int userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT name, phone_no, email, password FROM " + TABLE_USER;
        Cursor cursor = db.query(TABLE_USER, new String[]{COLUMN_USER_NAME, COLUMN_USER_PHONENO, COLUMN_USER_EMAIL, COLUMN_USER_PASSWORD}, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userid)}, null, null, null, null);
        if (cursor.moveToNext()) {
            HashMap<String, String> user = new HashMap<>();
            Log.d("Test", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)));
            user.put("name", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)));
            user.put("phoneNo", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONENO)));
            user.put("email", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)));
            user.put("password", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)));
            userList.add(user);
        }
        return userList;
    }

    // Update User Details
    public int UpdateUserDetails(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cVals = new ContentValues();
        cVals.put(COLUMN_USER_NAME, user.getName());
        cVals.put(COLUMN_USER_EMAIL, user.getEmail());
        cVals.put(COLUMN_USER_PASSWORD, user.getPassword());
        int count = db.update(TABLE_USER, cVals, COLUMN_USER_ID+" = ?",new String[]{String.valueOf(user.getId())});
        return  count;
    }
}
