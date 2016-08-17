package org.grameenfoundation.cch.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.fcannizzaro.materialstepper.AbstractStep;
import com.github.fcannizzaro.materialstepper.style.TabStepper;

import org.digitalcampus.mobile.learningGF.R;
import org.grameenfoundation.cch.fragments.AdditionalInformationFragment;
import org.grameenfoundation.cch.fragments.GenderOfChildFragment;
import org.grameenfoundation.cch.fragments.InitialRegistrationInfoFragment;
import org.grameenfoundation.cch.fragments.NoOfChildrenFragment;

public class UserRegistrationActivity extends TabStepper {

    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setErrorTimeout(1500);
        setLinear(false);
        setTitle("User Registration");
        setPrimaryColor(R.color.Green);
        setAlternativeTab(false);
        setDisabledTouch();
        setPreviousVisible();

        addStep(createFragment(new InitialRegistrationInfoFragment()));
        addStep(createFragment(new NoOfChildrenFragment()));
        addStep(createFragment(new GenderOfChildFragment()));
        addStep(createFragment(new AdditionalInformationFragment()));

        super.onCreate(savedInstanceState);
    }

    private AbstractStep createFragment(AbstractStep fragment) {
        Bundle b = new Bundle();
        b.putInt("position", i++);
        fragment.setArguments(b);
        return fragment;
    }

}
