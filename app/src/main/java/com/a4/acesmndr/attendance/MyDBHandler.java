package com.a4.acesmndr.attendance;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by acesmndr on 6/24/15.
 */
public class MyDBHandler extends SQLiteOpenHelper{
    private ContentResolver myCR;
    private static final int DATABASE_VERSION=2;
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
            values.put(COLUMN_NAMEOFCLASS,session.getClassName());
            values.put(COLUMN_ROLLSTART, session.getRollStart());
            values.put(COLUMN_NOS, session.getNoS());
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(TABLE_NAME, null, values);
            db.close();
            session=findSession(session.getClassName());
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
            String query_delete="DROP TABLE IF EXISTS class"+session.getID();
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
        String query_create="CREATE TABLE class"+session.getID()+" (id INTEGER PRIMARY KEY AUTOINCREMENT,dateToday VARCHAR NOT NULL";
        for(int i=0;i<session.getNoS();i++){
            query_create+=" ,s"+i+" BOOLEAN NOT NULL ";
        }
        query_create+=")";
        db.execSQL(query_create);
        db.close();
    }
    public boolean openClass(String nameOfClass){
        Session session=findSession(nameOfClass);
        SQLiteDatabase db=getWritableDatabase();
        String query_open="SELECT * FROM class"+session.getID()+" WHERE dateToday ='"+getDate()+"'";
        Cursor cursor=db.rawQuery(query_open,null);
        if(cursor.moveToFirst()){ //test whether the attendance sheet of particular day exists
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        query_open="INSERT INTO class"+session.getID()+" VALUES(NULL,'"+getDate()+"'"; //if not exists add  one
        for(int i=0;i<session.getNoS();i++){
            query_open+=",0";
        }
        query_open+=")";
        db.execSQL(query_open);
        db.close();
        return false;

    }
    public String getDate(){
        Date Dtoday=new Date();
        SimpleDateFormat df=new SimpleDateFormat("MMM d");
        return df.format(Dtoday);
    }
    public void presentdb(String tableName,int roll){
        Session session=findSession(tableName);
        SQLiteDatabase db=getWritableDatabase();
        String query="UPDATE class"+session.getID()+" SET s"+roll+"=1 WHERE dateToday='"+getDate()+"'";
        db.execSQL(query);
        db.close();
    }
    public void absentdb(String tableName,int roll){
        Session session=findSession(tableName);
        SQLiteDatabase db=getWritableDatabase();
        String query="UPDATE class"+session.getID()+" SET s"+roll+"=0 WHERE dateToday='"+getDate()+"'";
        db.execSQL(query);
        db.close();
    }
    public boolean checkPresence(String tableName,int roll){
        boolean presence=true;
        Session session=findSession(tableName);
        SQLiteDatabase db=getWritableDatabase();
        String query="SELECT s"+roll+" FROM class"+session.getID()+" WHERE dateToday='"+getDate()+"'";
        Cursor cursor = db.rawQuery(query, null);
        try {
            cursor.moveToFirst();
            if (cursor.getInt(0) == 0) {
                presence = false;
            }
        }catch (CursorIndexOutOfBoundsException e){
            presence = false;
        }
        cursor.close();
        db.close();
        return presence;
    }
    public int iWasPresentFor(String tableName,int roll){
        Session session=findSession(tableName);
        SQLiteDatabase db=getWritableDatabase();
        String query="SELECT COUNT(*) as countPresence FROM class"+session.getID()+" WHERE s"+roll+"=1";
        Cursor cursor=db.rawQuery(query,null);
        cursor.moveToFirst();
        int c=cursor.getInt(0);
        cursor.close();
        db.close();
        return c;
    }
    public int attendanceDoneFor(String tableName){
        Session session=findSession(tableName);
        SQLiteDatabase db=getWritableDatabase();
        String query="SELECT COUNT(*) as countPresence FROM class"+session.getID();
        Cursor cursor=db.rawQuery(query,null);
        cursor.moveToFirst();
        int c=cursor.getInt(0);
        cursor.close();
        db.close();
        return c;
    }
    public int entry(String tableName){
        Session session=findSession(tableName);
        SQLiteDatabase db=getWritableDatabase();
        String query="SELECT * FROM class"+session.getID();
        Cursor cursor = db.rawQuery(query, null);
        int counter = cursor.getCount();
        cursor.close();
        db.close();
        return counter;
    }
    public void addRandom(String tableName){
        Session session=findSession(tableName);
        //Date Dtoday=new Date();
        SQLiteDatabase db=getWritableDatabase();
        String query_open="INSERT INTO class"+session.getID()+" VALUES(NULL,'"+System.currentTimeMillis()%1000000000+"'"; //if not exists add  one
        for(int i=0;i<session.getNoS();i++){
            query_open+=",1";
        }
        query_open+=")";
        db.execSQL(query_open);
        db.close();
    }
    public int[] totalAttendance(String tableName){
        Session session=findSession(tableName);
        int[] count=new int[session.getNoS()];
        SQLiteDatabase db=getReadableDatabase();
        String query="SELECT * FROM class"+session.getID();
        Cursor cursor=db.rawQuery(query, null);
        while(cursor.moveToNext()){
            for(int j=2;j<cursor.getColumnCount();j++) { // columnCount-1 for one of column is date
                count[j-2]+=cursor.getInt(j);
            }
        }
        return count;

    }
    public String[][] dataToExport(String tableName){
        int[] count=totalAttendance(tableName);
        Session session=findSession(tableName);
        int rollStart=session.getRollStart();
        SQLiteDatabase db=getReadableDatabase();
        String query="SELECT * FROM class"+session.getID();
        Cursor cursor=db.rawQuery(query, null);
        int dbDays=cursor.getCount();
        int dbColumn=cursor.getColumnCount();
        String[][] register=new String[dbColumn-1][dbDays+2];
        if (cursor != null) {
                register[0][0]=" ";
                register[0][dbDays+1]="Total";
            for(int k=0;k<dbColumn-2;k++){
                register[k+1][0]=Integer.toString(rollStart+k);
                register[k+1][dbDays+1]=Integer.toString(count[k]);
            }
            int i = 1;
            while(cursor.moveToNext()){
                register[0][i]=cursor.getString(1);
                for(int j=1;j<dbColumn-1;j++) { // columnCount-1 for one of column is date
                    register[j][i] = cursor.getString(j + 1);
                    }
                i++;
                }
        }
        cursor.close();
        db.close();
        return register;
        }
    public String[][] registerShow(String tableName){
        Session session=findSession(tableName);
        SQLiteDatabase db=getReadableDatabase();
        String query="SELECT * FROM class"+session.getID()+" ORDER BY id DESC";
        Cursor cursor=db.rawQuery(query,null);
        int dbDays=cursor.getCount();
        int dbStudents=cursor.getColumnCount();
        if(dbDays>6)
            dbDays=6;
        String[][] register=new String[dbStudents-1][dbDays];
        if (cursor != null) {
            int i = 0;
            while(i<dbDays){
                cursor.moveToNext();
                register[0][i]=cursor.getString(1);
                for(int j=2;j<dbStudents;j++) { // columnCount-1 for one of column is date
                    register[j-1][i] = cursor.getString(j);
                }
                i++;
            }
        }
        cursor.close();
        db.close();
        return register;
    }
    public int getPresentTotal(String tableName){
        Session session=findSession(tableName);
        SQLiteDatabase db=getReadableDatabase();
        String query="SELECT * FROM class"+session.getID()+" WHERE dateToday='"+getDate()+"'";
        Cursor cursor=db.rawQuery(query, null);
        cursor.moveToFirst();
        int counter=0;
        for(int i=0;i<cursor.getColumnCount()-2;i++){
            counter+=cursor.getInt(i+2);
        }
        cursor.close();
        db.close();
        return counter;

    }



    }


