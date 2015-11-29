package com.a4.acesmndr.attendance;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class Register extends ActionBarActivity {
    TableLayout mainTable;
    TableRow overallRowData;
    TableLayout dataTable;
    TableRow rowData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        MyDBHandler dbHandler=new MyDBHandler(this,null,null,1);
        String[][] data=dbHandler.registerShow(getIntent().getExtras().getString("nameOfClass"));
        Session session=dbHandler.findSession(getIntent().getExtras().getString("nameOfClass"));
        int rollListIndex=session.getRollStart();
        //Toast.makeText(this,getIntent().getExtras().getString("nameOfClass"),Toast.LENGTH_LONG).show();
        setTitle(getIntent().getExtras().getString("nameOfClass"));
        int[] totalAttendance=getAttendanceCount(getIntent().getExtras().getString("nameOfClass"));
        mainTable=(TableLayout) findViewById(R.id.mainTable);

        //---------------Serial no Table Header-----------------------------------------------
        TableRow srno_tr_head = new TableRow(this);
        srno_tr_head.setBackgroundResource(R.drawable.cell_shape);
        srno_tr_head.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));


        TextView label_sr_no = new TextView(this);
        label_sr_no.setText("Register");
        label_sr_no.setTextColor(Color.WHITE);
        label_sr_no.setPadding(20,10,20,10);
        srno_tr_head.addView(label_sr_no);// add the column to the table row here
        //label_sr_no.setTextSize(20);



        mainTable.addView(srno_tr_head, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        //---------------Serial no Table Header-----------------------------------------------

        dataTable=(TableLayout) findViewById(R.id.dataTable);

        //---------------report Table Header-----------------------------------------------
        TableRow report_tr_head = new TableRow(this);
        report_tr_head.setBackgroundResource(R.drawable.cell_shape);
        report_tr_head.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        //heading names
        for(int i=0;i<data[0].length;i++) {
                TextView label_test_name = new TextView(this);
                label_test_name.setText(data[0][i]);
                label_test_name.setTextColor(Color.WHITE);
                label_test_name.setPadding(20,10,20,10);
                //label_test_name.setTextSize(20);
                report_tr_head.addView(label_test_name);// add the column to the table row here
                 }

        TextView label_test_name = new TextView(this);
        label_test_name.setText("Total");
        label_test_name.setTextColor(Color.WHITE);
        label_test_name.setPadding(20,10,20,10);
        //label_test_name.setTextSize(20);
        report_tr_head.addView(label_test_name);


        dataTable.addView(report_tr_head, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        //---------------Serial no Table Header-----------------------------------------------



        //--------------------Sr No Table Body---------------------------
        for (int i=1; i<data.length; i++)
        {
            overallRowData = new TableRow(this);
            overallRowData.setBackgroundColor(Color.TRANSPARENT);
            overallRowData.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            final TextView rollNo = new TextView(this);
            rollNo.setText(Integer.toString(rollListIndex++));
            rollNo.setTextColor(Color.WHITE);
            rollNo.setBackgroundResource(R.drawable.cell_shape);
            rollNo.setPadding(30, 10, 30, 10);
            rollNo.setGravity(Gravity.CENTER_HORIZONTAL);
            overallRowData.addView(rollNo);// add the column to the table row here


            mainTable.addView(overallRowData, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
  overallRowData.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
      }
  });
           }
    for (int i=1; i<=data.length-1; i++)
        {
            rowData=new TableRow(this);
            rowData.setBackgroundColor(Color.TRANSPARENT);
            rowData.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            for(int j=0;j<data[0].length+1;j++) {
                try {
                    final TextView test_name = new TextView(this);
                    if (data[i][j].equals("1")) {
                        test_name.setBackgroundResource(R.drawable.present_shape);
                    } else {
                        test_name.setBackgroundResource(R.drawable.absent_shape);
                    }
                    test_name.setPadding(20,10,20,10);
                    rowData.addView(test_name);// add the column to the table row here
                }catch (ArrayIndexOutOfBoundsException e){
                    final TextView test_name = new TextView(this);
                    test_name.setPadding(20,10,20,10);
                    test_name.setBackgroundResource(R.drawable.cell_shape);
                    test_name.setText(Integer.toString(totalAttendance[i - 1]));
                    test_name.setTextColor(Color.WHITE);
                    test_name.setGravity(Gravity.CENTER_HORIZONTAL);
                    rowData.addView(test_name);// add the column to the table row here
                }

            }
            dataTable.addView(rowData, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            //----------------------On click table row---------------------------------------

        }
    }
    public int[] getAttendanceCount(String nameOfClass){
        MyDBHandler dbHandler=new MyDBHandler(this,null,null,1);
        return dbHandler.totalAttendance(nameOfClass);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);

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
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_version)+"Bug Report");
                    intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"a4developers@gmail.com"});
                    this.startActivity(Intent.createChooser(intent, "Report a Bug via Email"));
                    return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
