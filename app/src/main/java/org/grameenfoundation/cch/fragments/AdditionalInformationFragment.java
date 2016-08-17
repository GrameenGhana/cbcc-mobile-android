package org.grameenfoundation.cch.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.github.fcannizzaro.materialstepper.AbstractStep;

import org.digitalcampus.mobile.learningGF.R;
import org.grameenfoundation.cch.utils.MaterialSpinner;


public class AdditionalInformationFragment extends AbstractStep {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int i = 0;
    private final static String CLICK = "click";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MaterialSpinner education;


    public AdditionalInformationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdditionalInformationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdditionalInformationFragment newInstance(String param1, String param2) {
        AdditionalInformationFragment fragment = new AdditionalInformationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(CLICK, i);
    }

    @Override
    public String name() {
        String[] names = new String[]{"Initial Info", "No of Children",
                "Date of Birth", "Gender of Children",
                "Additional Information"};
        //  return names[getArguments().getInt("position", 0)] ;
        return "Additional Information";
    }

    @Override
    public boolean isOptional() {
        return true;
    }


    @Override
    public void onStepVisible() {
    }

    @Override
    public void onNext() {
        System.out.println("onNext");
    }

    @Override
    public void onPrevious() {
        System.out.println("onPrevious");
    }

       /* @Override
        public String optional() {
            return "You can skip";
        }*/

    @Override
    public boolean nextIf() {
        return i > 1;
    }

    @Override
    public String error() {
        return "<b>You must click!</b> <small>this is the condition!</small>";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_additional_information, container, false);
        ArrayAdapter<String> educationAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.education));
        educationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        education = (MaterialSpinner) v.findViewById(R.id.education);
        education.setAdapter(educationAdapter);
        return v;
    }


}
