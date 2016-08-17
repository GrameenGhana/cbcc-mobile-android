package org.grameenfoundation.cch.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.github.fcannizzaro.materialstepper.AbstractStep;

import org.digitalcampus.mobile.learningGF.R;
import org.grameenfoundation.cch.utils.MaterialSpinner;

public class InitialRegistrationInfoFragment extends AbstractStep {

    private int i = 0;
    private Button button;
    private final static String CLICK = "click";
    private MaterialSpinner gender;
    private MaterialSpinner education;
    private MaterialSpinner marital_status;
    private MaterialSpinner occupation;
    private MaterialSpinner pregnancy_status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_user_registration, container, false);
         
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.gender));
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender = (MaterialSpinner) v.findViewById(R.id.gender);
        gender.setAdapter(genderAdapter);

        ArrayAdapter<String> educationAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.education));
        educationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        education = (MaterialSpinner) v.findViewById(R.id.education);
        education.setAdapter(educationAdapter);

        ArrayAdapter<String> maritalAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.marital_status));
        maritalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        marital_status = (MaterialSpinner) v.findViewById(R.id.marital_status);
        marital_status.setAdapter(maritalAdapter);

        ArrayAdapter<String> occupationAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.occupation));
        occupationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        occupation = (MaterialSpinner) v.findViewById(R.id.occupation);
        occupation.setAdapter(occupationAdapter);

        ArrayAdapter<String> pregnancyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.pregnancy_status));
        pregnancyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pregnancy_status = (MaterialSpinner) v.findViewById(R.id.pregnancy_status);
        pregnancy_status.setAdapter(pregnancyAdapter);

           /* button = (Button) v.findViewById(R.id.button);

            if (savedInstanceState != null)
                i = savedInstanceState.getInt(CLICK, 0);

            button.setText(Html.fromHtml("Tap <b>" + i + "</b>"));

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Button) view).setText(Html.fromHtml("Tap <b>" + (++i) + "</b>"));
                    mStepper.getExtras().putInt(CLICK, i);
                }
            });*/

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(CLICK, i);
    }

    @Override
    public String name() {
        String[] names=new String[]{"Initial Info","No of Children",
                "Date of Birth","Gender of Children",
                "Additional Information"};
       // return names[getArguments().getInt("position", 0)] ;
        return "Initial Info";
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

}
