package com.example.acesmndr.attendance;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by acesmndr on 6/24/15.
 */
public class MyDBHandler extends SQLiteOpenHelper{
    private ContentResolver myCR;
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME = "attendanceDB.db";
    public static final String TABLE_NAME = "classes";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAMEOFCLASS = "nameOfClass";
    public static final String COLUMN_ROLLSTART = "rollStart";
    public static final String COLUMN_NOS="noS";

    public MyDBHandler(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context,DATABASE_NAME,factory,DATABASE_VERSION);
        myCR=context.getContentResolver();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CLASS_TABLE="CREATE TABLE "+TABLE_NAME+"( "+COLUMN_ID+" INTEGER PRIMARY KEY,"+COLUMN_NAMEOFCLASS+" TEXT,"+COLUMN_ROLLSTART+" INTEGER,"+COLUMN_NOS+" INTEGER"+")";
        db.execSQL(CREATE_CLASS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public boolean addSession(Session session){
        if(findSession(session.getClassName())==null) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAMEOFCLASS, session.getClassName());
            values.put(COLUMN_ROLLSTART, session.getRollStart());
            values.put(COLUMN_NOS, session.getNoS());
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(TABLE_NAME, null, values);
            db.close();
            createClassTable(session);
            //openClass(session.getClassName());
            return true;
            }else {
            return false;
            //Log.d("cow","Exists");
        }
    }
    public Session findSession(String nameOfClass){
        String query="Select * FROM "+TABLE_NAME+" WHERE "+COLUMN_NAMEOFCLASS+" = '"+nameOfClass+"'";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(query, null);
        Session session=new Session();
        if(cursor.moveToFirst()){
            cursor.moveToFirst();
            session.setID(Integer.parseInt(cursor.getString(0)));
            session.setClassName(cursor.getString(1));
            session.setRollStart(Integer.parseInt(cursor.getString(2)));
            session.setNoS(Integer.parseInt(cursor.getString(3)));
            cursor.close();
        }else{
            session=null;
        }
        db.close();
        return session;
    }
    public boolean deleteSession(String nameOfClass){
        boolean result=false;
        String query="Select * FROM "+TABLE_NAME+" WHERE "+COLUMN_NAMEOFCLASS+" = '"+nameOfClass+"'";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(query, null);
        Session session=new Session();
        if(cursor.moveToFirst()){
            session.setID(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_NAME, COLUMN_ID + "= ?", new String[]{String.valueOf(session.getID())});
            cursor.close();
            String query_delete="DROP TABLE IF EXISTS "+nameOfClass;
            db.execSQL(query_delete);
            result=true;
        }
        db.close();
        return result;
    }
    public List<Session> getAllClasses() {
        List<Session> session = new ArrayList<Session>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM classes", null);

        if (cursor.moveToFirst()) {
            do {
                session.add(new Session(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3))));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return session;
    }
    public String[] getAllClassesA() {
        String data[]=new String[getSessionCount()];
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM classes", null);
        if (cursor != null) {
            int i = 0;
            while(cursor.moveToNext()){
                                Log.d("cow", "a"+i);
                               data[i] = cursor.getString(1);
                                i=i+1;
                               //cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return data;
    }
    public int getSessionCount(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM classes", null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        return count;
    }
    public void createClassTable(Session session){
        SQLiteDatabase db=getWritableDatabase();
        String query_create="CREATE TABLE "+session.getClassName()+" (dateToday VARCHAR NOT NULL";
        for(int i=0;i<session.getNoS();i++){
            query_create+=" ,s"+i+" BOOLEAN NOT NULL ";
        }
        query_create+=")";
        db.execSQL(query_create);
        db.close();
    }
    public void openClass(String nameOfClass){
        Session session=findSession(nameOfClass);
        SQLiteDatabase db=getWritableDatabase();
        String query_open="SELECT * FROM "+nameOfClass+" WHERE dateToday ='"+getDate()+"'";
        Cursor cursor=db.rawQuery(query_open,null);
        if(cursor.moveToFirst()){ //test whether the attendance sheet of particular day exists
            cursor.close();
            return;
        }
        cursor.close();
        query_open="INSERT INTO "+nameOfClass+" VALUES('"+getDate()+"'"; //if not exists add  one
        for(int i=0;i<session.getNoS();i++){
            query_open+=",0";
        }
        query_open+=")";
        db.execSQL(query_open);
        db.close();

    }
    public String getDate(){
        Date Dtoday=new Date();
        SimpleDateFormat df=new SimpleDateFormat("MMM d");
        return df.format(Dtoday);
    }
    public void presentdb(String tableName,int roll){
        SQLiteDatabase db=getWritableDatabase();
        String query="UPDATE "+tableName+" SET s"+roll+"=1 WHERE dateToday='"+getDate()+"'";
        db.execSQL(query);
        db.close();
    }
    public void absentdb(String tableName,int roll){
        SQLiteDatabase db=getWritableDatabase();
        String query="UPDATE "+tableName+" SET s"+roll+"=0 WHERE dateToday='"+getDate()+"'";
        db.execSQL(query);
        db.close();
    }
}
