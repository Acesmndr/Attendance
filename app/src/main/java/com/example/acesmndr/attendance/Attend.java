package com.example.acesmndr.attendance;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Attend extends ActionBarActivity {
    SeekBar sb;
    TextView rollDisplay;
    Button present;
    Button absent;
    int roll,noS;
    String currentTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend);
        //Toast.makeText(Attend.this, getIntent().getExtras().getString("className"), Toast.LENGTH_SHORT).show();
        Session session=getCurrentSession(getIntent().getExtras().getString("className"));
        currentTable=session.getClassName();
        initiate();
        setTitle(currentTable);
        roll=session.getRollStart();
        noS=session.getNoS();
        /*Date Dtoday=new Date();
        SimpleDateFormat df=new SimpleDateFormat("MMM d");
        Toast.makeText(this,df.format(Dtoday),Toast.LENGTH_SHORT).show();*/
        rollDisplay= (TextView) findViewById(R.id.rollDisplay);
        present= (Button) findViewById(R.id.present);
        absent= (Button) findViewById(R.id.absent);
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
        rollDisplay.setText(Integer.toString(roll));
        sb= (SeekBar) findViewById(R.id.seekBar2);
        sb.setMax(noS-1);
        sb.setProgress(0);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rollDisplay.setText(Integer.toString(progress+roll));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    public void initiate(){
        MyDBHandler dbHandler=new MyDBHandler(Attend.this,null,null,1);
        dbHandler.openClass(currentTable);
    }
    public Session getCurrentSession(String nameOfClass){
        MyDBHandler dbHandler=new MyDBHandler(Attend.this,null,null,1);
        return dbHandler.findSession(nameOfClass);
    }
    public void progress(int ap,int progress){
        if(ap==0) {
            markAbsent(progress);
        }else{
            markPresent(progress);
        }
        if(progress==(noS-1)){
            Toast.makeText(Attend.this,"Attendance Completed",Toast.LENGTH_SHORT).show();
        }
        sb.setProgress(progress+1);
    }
    public void markPresent(int sId){
        MyDBHandler dbHandler=new MyDBHandler(Attend.this,null,null,1);
        dbHandler.presentdb(currentTable,sId);
    }
    public void markAbsent(int sId){
        MyDBHandler dbHandler=new MyDBHandler(Attend.this,null,null,1);
        dbHandler.absentdb(currentTable,sId);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
