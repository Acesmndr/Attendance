package com.example.acesmndr.attendance;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddClass.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddClass#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddClass extends Fragment {

    EditText nameOfClassX;
    EditText rollStartX;
    SeekBar noSX;
    TextView noSDisplayX;
    Button addButton,deleteButton,searchButton,register,list;
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
     * @return A new instance of fragment AddClass.
     */
    // TODO: Rename and change types and number of parameters
    public static AddClass newInstance(String param1, String param2) {
        AddClass fragment = new AddClass();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AddClass() {
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
        View view=inflater.inflate(R.layout.fragment_add_class,container,false);
        // Inflate the layout for this fragment
        Toast.makeText(getActivity(),"Hello",Toast.LENGTH_SHORT).show();
        nameOfClassX= (EditText) view.findViewById(R.id.editText);
        rollStartX = (EditText) view.findViewById(R.id.editText2);
        noSX = (SeekBar) view.findViewById(R.id.seekBar);
        noSDisplayX = (TextView) view.findViewById(R.id.textView4);
        addButton= (Button) view.findViewById(R.id.addButton);
        deleteButton= (Button) view.findViewById(R.id.deleteButton);
        searchButton= (Button) view.findViewById(R.id.searchButton);
        register= (Button) view.findViewById(R.id.register);
        list= (Button) view.findViewById(R.id.listButton);
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDBHandler dbHandler=new MyDBHandler(getActivity(),null,null,1);
                dbHandler.addRandom(nameOfClassX.getText().toString());
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newSession(v);

            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSession(v);
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lookUpSession(v);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"a4developers@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Attendance List of a Class");
                intent.putExtra(Intent.EXTRA_TEXT, "Attendance done by attendance application developed by A4 developers");
                File root = Environment.getExternalStorageDirectory();
                File file = new File(root, "/Download/aces.csv");
                if (!file.exists() || !file.canRead()) {
                    Toast.makeText(getActivity(), "Attachment Error", Toast.LENGTH_SHORT).show();
                    //finish();
                    return;
                }
                Uri uri = Uri.parse("file://" + file.getAbsolutePath());
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intent, "Send via Email"));

            }
        });
        noSX.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                noSDisplayX.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }

    public void newSession(View view){
        //Toast.makeText(getActivity(),"Aces loves sima",Toast.LENGTH_LONG).show();
        MyDBHandler dbHandler=new MyDBHandler(getActivity(),null,null,1);
        Session session=new Session( nameOfClassX.getText().toString(),Integer.parseInt(rollStartX.getText().toString()),noSX.getProgress());
        boolean check=dbHandler.addSession(session);
        if(check==true){
            Intent intent=new Intent(getActivity(),Attend.class);
            intent.putExtra("className",nameOfClassX.getText().toString());
            startActivity(intent);
            return;
        }
        Toast.makeText(getActivity(), "Class already exists! Try with a different Name", Toast.LENGTH_LONG).show();
        nameOfClassX.setText("");
        rollStartX.setText("1");
        noSX.setProgress(44);
        noSDisplayX.setText("44");
    }
    public void lookUpSession(View view){
        MyDBHandler dbHandler=new MyDBHandler(getActivity(),null,null,1);
        Session session=dbHandler.findSession(nameOfClassX.getText().toString());
        Log.d("Aces", nameOfClassX.getText().toString());
        if(session != null){
            rollStartX.setText(String.valueOf(session.getRollStart()));
            noSX.setProgress(Integer.parseInt(String.valueOf(session.getNoS())));
        }else{
            nameOfClassX.setText("No Such Class");
        }
    }
    public void removeSession(View view){
        MyDBHandler dbHandler=new MyDBHandler(getActivity(),null,null,1);
        boolean result=dbHandler.deleteSession(nameOfClassX.getText().toString());
        if(result)
        {
            nameOfClassX.setText("Class Deleted");
        }else{
            nameOfClassX.setText("No Match Found");
        }
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
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
