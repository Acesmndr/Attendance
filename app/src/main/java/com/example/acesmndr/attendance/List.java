package com.example.acesmndr.attendance;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link List.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link List#newInstance} factory method to
 * create an instance of this fragment.
 */
public class List extends Fragment {

    ArrayList<Session> Sessions = new ArrayList<Session>();
    ListView sessionListView;
    MyDBHandler dbsHandler;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment List.
     */
    // TODO: Rename and change types and number of parameters
    public static List newInstance(String param1, String param2) {
        List fragment = new List();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public List() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_list,container,false);
        //setTitle("Attendance:Select a Class");
        sessionListView= (ListView) view.findViewById(R.id.listView);
        dbsHandler=new MyDBHandler(getActivity(),null,null,1);
        if(dbsHandler.getSessionCount()!=0)
            Toast.makeText(getActivity(),"Hello", Toast.LENGTH_LONG).show();
        String[] val;
        val=dbsHandler.getAllClassesA();
        //Toast.makeText(List.this,val[0]+"loves"+val[1],Toast.LENGTH_SHORT).show();
        if(canWriteOnExternalStorage()){
            // get the path to sdcard
            File sdcard = Environment.getExternalStorageDirectory();
            File dir = new File(sdcard.getAbsolutePath() + "/downloads/");// to this path add a new directory path
            dir.mkdir();// create this directory if not already created
            File file = new File(dir, "aces.csv");// create the file in which we will write the contents
            FileOutputStream os = null;
            try {
                os = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String data = "latitude,longitude,HQ_NAME\n" +
                    "29.97531509,81.81872559,Simikot\n" +
                    "29.84737968,80.57302856,Darchula";
            try {
                os.write(data.getBytes());
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        };
        String[] values={"Hello","Sima"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, val);
        sessionListView.setAdapter(adapter);

        // ListView Item Click Listener
        sessionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) sessionListView.getItemAtPosition(position);
                Intent intent=new Intent(getActivity(),Attend.class);
                intent.putExtra("className",itemValue);
                startActivity(intent);
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
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
