package com.example.acesmndr.attendance;

/**
 * Created by acesmndr on 6/24/15.
 */
public class Session {
    private int _id;
    private String _nameOfClass;
    private int _rollStart;
    private int _noS;
    public Session(){
    }
    public Session(int id,String nameOfClass,int rollStart,int noS){
        this._id=id;
        this._nameOfClass=nameOfClass;
        this._rollStart=rollStart;
        this._noS=noS;
    }
    public Session(String nameOfClass,int rollStart,int noS){
        this._nameOfClass=nameOfClass;
        this._rollStart=rollStart;
        this._noS=noS;
    }
    public void setID(int id){
        this._id=id;
    }
    public int getID(){
        return this._id;
    }
    public void setClassName(String nameOfClass){
        this._nameOfClass=nameOfClass;
    }
    public String getClassName(){
        return this._nameOfClass;
    }
    public void setRollStart(int rollStart){
        this._rollStart=rollStart;
    }
    public int getRollStart(){
        return this._rollStart;
    }
    public void setNoS(int noS){
        this._noS=noS;
    }
    public int getNoS(){
        return this._noS;
    }
}
