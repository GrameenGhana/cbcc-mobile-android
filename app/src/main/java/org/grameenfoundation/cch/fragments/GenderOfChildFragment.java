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
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.fcannizzaro.materialstepper.AbstractStep;

import org.digitalcampus.mobile.learningGF.R;
import org.grameenfoundation.cch.utils.MaterialSpinner;

public class GenderOfChildFragment extends AbstractStep {

    private int i = 0;
    private Button button;
    private final static String CLICK = "click";
    private Button buttonAdd;
    private LinearLayout container;
    private MaterialSpinner gender;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup cont, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_gender_of_child, cont, false);
        buttonAdd = (Button) v.findViewById(R.id.add);
        container = (LinearLayout) v.findViewById(R.id.container);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.gender));
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender = (MaterialSpinner) v.findViewById(R.id.gender);
        gender.setAdapter(genderAdapter);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.row2, null);
                //MaterialSpinner textOut = (MaterialSpinner) addView.findViewById(R.id.gender);
                //textOut.setText(textIn.getText().toString());
                ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.gender));
                genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                MaterialSpinner textOut = (MaterialSpinner) addView.findViewById(R.id.gender);
                textOut.setAdapter(genderAdapter);

                Button buttonRemove = (Button) addView.findViewById(R.id.remove);

                final View.OnClickListener thisListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ((LinearLayout) addView.getParent()).removeView(addView);

                        listAllAddView();
                    }
                };

                buttonRemove.setOnClickListener(thisListener);
                container.addView(addView);


                listAllAddView();
            }
        });


        return v;
    }

    private void listAllAddView() {


        int childCount = container.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View thisChild = container.getChildAt(i);


            ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.gender));
            genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            MaterialSpinner textOut = (MaterialSpinner) thisChild.findViewById(R.id.gender);
            textOut.setAdapter(genderAdapter);
           // String childTextViewValue = childTextView.getText().toString();


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
        return "Gender of Children";
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