package com.a4.acesmndr.attendance;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class Attend extends ActionBarActivity {
    SeekBar sb;
    TextView rollDisplay;
    Button present;
    Button absent;
    Button register;
    TextView daysPresent;
    int roll,noS,noDays;
    boolean virginity=true; //check if today's class has been already added
    String currentTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_attend);

        Session session=getCurrentSession(getIntent().getExtras().getString("nameOfClass"));
        currentTable=session.getClassName();
        //initiate();
        setTitle(currentTable);
        roll=session.getRollStart();
        noS=session.getNoS();
        noDays=attendanceDays();
        rollDisplay= (TextView) findViewById(R.id.rollDisplay);
        daysPresent=(TextView) findViewById(R.id.daysPresent);
        present= (Button) findViewById(R.id.present);
        absent= (Button) findViewById(R.id.absent);
        register=(Button) findViewById(R.id.registerButton);
        present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress(1,sb.getProgress());
            }
        });
        absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress(0,sb.getProgress());
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Attend.this,Register.class);
                intent.putExtra("nameOfClass",currentTable);
                Attend.this.startActivity(intent);

            }
        });
        rollDisplay.setText(Integer.toString(roll));
        sb= (SeekBar) findViewById(R.id.seekBar2);
        sb.setMax(noS-1);
        sb.setProgress(0);
        checkIfPresent(0);
        daysPresent.setText(noOfDaysPresent(0) + " out of " + noDays + " days!");
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rollDisplay.setText(Integer.toString(progress + roll));


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                rollDisplay.setTextColor(Color.BLACK);
                daysPresent.setText("Experimental Feature!");

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                checkIfPresent(sb.getProgress());
                daysPresent.setText(noOfDaysPresent(sb.getProgress())+" out of "+noDays+" days!");

            }
        });
    }
    public void initiate(){
        MyDBHandler dbHandler=new MyDBHandler(Attend.this,null,null,1);
        if(!dbHandler.openClass(currentTable)){
            noDays+=1;
        }
        virginity=false;
    }
    public Session getCurrentSession(String nameOfClass){
        MyDBHandler dbHandler=new MyDBHandler(Attend.this,null,null,1);
        return dbHandler.findSession(nameOfClass);
    }
    public void progress(int ap,int progress){
        if(virginity==true){
            initiate();
        }
        if(ap==0) {
            markAbsent(progress);
        }else{
            markPresent(progress);
        }
        Vibrator vibrator= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(150);
        if(progress==(noS-1)){
            int presentCount=getTotalPresent();
            NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification=new Notification(R.drawable.abc_ic_menu_paste_mtrl_am_alpha,"Attendance Completed:"+presentCount+"/"+noS,System.currentTimeMillis());
            Context context=Attend.this;
            CharSequence title="Attendance complete";
            CharSequence details=presentCount+" out of "+noS+" are Present";
            Intent intent=new Intent(context,Register.class);
            intent.putExtra("nameOfClass", currentTable);
            PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setLatestEventInfo(context, title, details, pendingIntent);
            notificationManager.notify(0, notification);
            vibrator.vibrate(350);
            checkIfPresent(progress);
            }else {
            checkIfPresent(progress + 1);
            daysPresent.setText(noOfDaysPresent(progress+1)+" out of "+noDays+" days!");
        }
        sb.setProgress(progress + 1);

    }
    public void markPresent(int sId){
        MyDBHandler dbHandler=new MyDBHandler(Attend.this,null,null,1);
        dbHandler.presentdb(currentTable, sId);


    }
    public void markAbsent(int sId){
        MyDBHandler dbHandler=new MyDBHandler(Attend.this,null,null,1);
        dbHandler.absentdb(currentTable, sId);

    }
    public void checkIfPresent(int sId){
        MyDBHandler dbHandler=new MyDBHandler(Attend.this,null,null,1);
        if(dbHandler.checkPresence(currentTable, sId)){
            rollDisplay.setTextColor(Color.parseColor("#0091EA"));
        }else{
            rollDisplay.setTextColor(Color.BLACK);
        }
    }
    public int attendanceDays(){
        MyDBHandler dbHandler=new MyDBHandler(Attend.this,null,null,1);
        return dbHandler.attendanceDoneFor(currentTable);
    }
    public int noOfDaysPresent(int sId){
        MyDBHandler dbHandler=new MyDBHandler(Attend.this,null,null,1);
        return dbHandler.iWasPresentFor(currentTable,sId);
    }

    public int getTotalPresent(){
        MyDBHandler dbHandler=new MyDBHandler(Attend.this,null,null,1);
        return dbHandler.getPresentTotal(currentTable);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_attend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Attendance v2.1.0:Bug Report");
            intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"a4developers@gmail.com"});
            this.startActivity(Intent.createChooser(intent, "Report a Bug via Email"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed(){
        int currentProg=sb.getProgress();
        if(currentProg!=0){
            sb.setProgress(currentProg-1);
            checkIfPresent(currentProg-1);
            daysPresent.setText(noOfDaysPresent(currentProg-1)+" out of "+noDays+" days!");
        }else{
            super.onBackPressed();
        }
    }

}
