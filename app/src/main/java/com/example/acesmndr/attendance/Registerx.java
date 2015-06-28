package com.example.acesmndr.attendance;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class Registerx extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerx);
        MyDBHandler dbHandler=new MyDBHandler(this,null,null,1);
        String[][] data=dbHandler.dataToExport("Acesa", 68002);
        TableLayout stk = (TableLayout) findViewById(R.id.table_main);
        for(int i=0;i<data.length;i++){
            TableRow tb=new TableRow(this);
            for(int j=0;j<data[0].length;j++){
                TextView tv=new TextView(this);
                tv.setText(data[i][j]);
                tv.setTextColor(Color.WHITE);
                tv.setGravity(Gravity.CENTER);
                tb.addView(tv);
            }
            stk.addView(tb);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registerx, menu);
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
