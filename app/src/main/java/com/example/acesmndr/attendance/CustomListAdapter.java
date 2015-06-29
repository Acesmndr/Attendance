package com.example.acesmndr.attendance;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by acesmndr on 6/29/15.
 */
public class CustomListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private int whichList;

    public CustomListAdapter(ArrayList<String> list, Context context,int whichList) {
        this.list = list;
        this.context = context;
        this.whichList = whichList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(whichList==1) {
                view = inflater.inflate(R.layout.list_item_layout, null);
            }else{
                view = inflater.inflate(R.layout.register_list_item_layout, null);
            }
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
        Button addBtn = (Button)view.findViewById(R.id.add_btn);

        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String itemValue = (String) getItem(position);
                Intent intent;
                if (whichList==1){
                    intent=new Intent(context, Attend.class);
                }else{
                    intent=new Intent(context, Register.class);
                }
                intent.putExtra("nameOfClass", itemValue);
                context.startActivity(intent);
                //do something
                //notifyDataSetChanged();
            }

        });
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (whichList == 1) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setMessage("Delete "+getItem(position).toString()+" ?")
                            .setCancelable(true)
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MyDBHandler dbHandler = new MyDBHandler(context, null, null, 1);
                                    dbHandler.deleteSession(getItem(position).toString());
                                    list.remove(position); //or some other task
                                    notifyDataSetChanged();
                                }
                            });
                    AlertDialog alertDialog=builder.create();
                    alertDialog.show();


                } else {
                    if (canWriteOnExternalStorage()) {
                        // get the path to sdcard
                        File sdcard = Environment.getExternalStorageDirectory();
                        File dir = new File(sdcard.getAbsolutePath() + "/Download/");// to this path add a new directory path
                        dir.mkdir();// create this directory if not already created
                        File file = new File(dir, getItem(position).toString() + ".csv");// create the file in which we will write the contents
                        FileOutputStream os = null;
                        try {
                            os = new FileOutputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        String data = writeExternal(getItem(position).toString());
                        try {
                            os.write(data.getBytes());
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        AlertDialog.Builder builder=new AlertDialog.Builder(context);
                        builder.setMessage(getItem(position).toString()+" CSV File Exported!\nDo you want to Email the file?")
                                .setCancelable(true)
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setType("text/html");
                                        intent.putExtra(Intent.EXTRA_SUBJECT, "Attendance Register of "+getItem(position).toString());
                                        intent.putExtra(Intent.EXTRA_TEXT, "attendance v2.0.0 beta \n A4 Developers");
                                        File root = Environment.getExternalStorageDirectory();
                                        File file = new File(root, "/Download/"+getItem(position).toString()+".csv");
                                        if (!file.exists() || !file.canRead()) {
                                            Toast.makeText(context, "Attachment Error", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        Uri uri = Uri.parse("file://" + file.getAbsolutePath());
                                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                                        context.startActivity(Intent.createChooser(intent, "Send via Email"));
                                    }
                                });
                        AlertDialog alertDialog=builder.create();
                        alertDialog.show();


                        }
                    }
            }
        });
        return view;
    }
    public static boolean canWriteOnExternalStorage() {
        // get the state of your external storage
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // if storage is mounted return true
            Log.v("sTag", "Yes, can write to external storage.");
            return true;
        }
        return false;
    }

    public String writeExternal(String nameOfClass){
        MyDBHandler dbHandler=new MyDBHandler(context,null,null,1);
        String[][] data=dbHandler.dataToExport(nameOfClass,68002);
        String toPrint=nameOfClass+"\nAttendance v2.0.0 Beta\n\n";
        for(int i=0;i<data.length;i++ ){
            for(int j=0;j<data[0].length;j++){
                if(j==0){
                    toPrint+=data[i][j];
                }else{
                    toPrint+=","+data[i][j];
                }
            }
            toPrint+="\n";
        }
        return toPrint;
    }
}
