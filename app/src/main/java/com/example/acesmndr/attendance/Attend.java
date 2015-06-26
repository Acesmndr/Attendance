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
    int roll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend);
        Toast.makeText(Attend.this, getIntent().getExtras().getString("className"), Toast.LENGTH_SHORT).show();
        MyDBHandler dbHandler=new MyDBHandler(this,null,null,1);
        Session session=dbHandler.findSession(getIntent().getExtras().getString("className"));
        setTitle(session.getClassName());
        Date Dtoday=new Date();
        SimpleDateFormat df=new SimpleDateFormat("MMM d");
        roll=session.getRollStart();
        Toast.makeText(this,df.format(Dtoday),Toast.LENGTH_SHORT).show();
        rollDisplay= (TextView) findViewById(R.id.rollDisplay);
        present= (Button) findViewById(R.id.present);
        absent= (Button) findViewById(R.id.absent);
        present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb.setProgress(sb.getProgress()+1);
            }
        });
        absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb.setProgress(sb.getProgress() + 1);
            }
        });
        rollDisplay.setText(Integer.toString(roll));
        sb= (SeekBar) findViewById(R.id.seekBar2);
        sb.setMax(session.getNoS()-1);
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
    public void markPresent(String tableName,int roll){
        /*MyDBHandler dbHandler=new MyDBHandler(Attend.this,null,null,1);
        query="UPDATE "+tableName+" SET s"+roll+"=1 WHERE dateToday="+dbHandler.getDate();
        dbHandler.*/
    }
    public void markAbsent(int roll){

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
