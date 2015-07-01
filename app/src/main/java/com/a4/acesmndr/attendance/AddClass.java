package com.a4.acesmndr.attendance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


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
    Button addButton,cancelButton;
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
        nameOfClassX= (EditText) view.findViewById(R.id.editText);
        rollStartX = (EditText) view.findViewById(R.id.editText2);
        noSX = (SeekBar) view.findViewById(R.id.seekBar);
        noSDisplayX = (TextView) view.findViewById(R.id.textView4);
        addButton= (Button) view.findViewById(R.id.addButton);
        cancelButton= (Button) view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameOfClassX.setText("");
                rollStartX.setText("1");
                noSX.setProgress(44);
                noSDisplayX.setText("44");
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameOfClassX.getText().toString().trim().length()>0){
                    if(rollStartX.getText().toString().length()>0){
                        newSession(v);
                    }
                     else{
                        Toast.makeText(getActivity(),"Starting Roll No Can't be Empty",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(),"Please enter a class name",Toast.LENGTH_SHORT).show();
                }

            }
        });
        /*deleteButton.setOnClickListener(new View.OnClickListener() {
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
        });*/
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
        MyDBHandler dbHandler=new MyDBHandler(getActivity(),null,null,1);
        int progress=noSX.getProgress();
        if(progress==0){
            progress=1;
        }
        Session session=new Session( nameOfClassX.getText().toString().trim(),Integer.parseInt(rollStartX.getText().toString()),progress);
        boolean check=dbHandler.addSession(session);
        if(check==true){
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            builder.setMessage("Do you want to add Another Class?")
                    .setCancelable(true)
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent=new Intent(getActivity(),MainActivity.class);
                            startActivity(intent);
                            return;
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            nameOfClassX.setText("");
                            rollStartX.setText("1");
                            noSX.setProgress(44);
                            noSDisplayX.setText("44");
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
            return;
        }
        Toast.makeText(getActivity(), "Class already exists! Try with a different Name", Toast.LENGTH_LONG).show();
        nameOfClassX.setText("");
    }
    /*public void lookUpSession(View view){
        MyDBHandler dbHandler=new MyDBHandler(getActivity(),null,null,1);
        Session session=dbHandler.findSession(nameOfClassX.getText().toString());
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
    }*/

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
