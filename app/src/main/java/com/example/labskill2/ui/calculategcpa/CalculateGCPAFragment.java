package com.example.labskill2.ui.calculategcpa;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.labskill2.R;

public class CalculateGCPAFragment extends Fragment {

    EditText currentGPAET;
    EditText targetGPAET;
    EditText currentCreditET;
    EditText additionalCreditET;
    Button calculateBtn;
    Button clearBtn;
    LinearLayout resultLayout;
    TextView resultTV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calculate_gcpa, container, false);

        currentGPAET = (EditText) view.findViewById(R.id.currentGPAEditText);
        targetGPAET = (EditText) view.findViewById(R.id.targetGPAEditText);
        currentCreditET = (EditText) view.findViewById(R.id.currentCreditEditText);
        additionalCreditET = (EditText) view.findViewById(R.id.additionalCreditEditText);
        calculateBtn = (Button) view.findViewById(R.id.calculateButton);
        clearBtn = (Button) view.findViewById(R.id.clearButton);
        resultLayout = (LinearLayout) view.findViewById(R.id.resultLayout);
        resultTV = (TextView) view.findViewById(R.id.resultTextView);

        calculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultLayout.setVisibility(view.VISIBLE);

                String currentGPA = currentGPAET.getText().toString();
                String targetGPA = targetGPAET.getText().toString();
                String currentCredit = currentCreditET.getText().toString();
                String additionalCredit = additionalCreditET.getText().toString();

                if(currentGPA.isEmpty()) {
                    resultTV.setText("current GPA is Empty!");
                } else if(targetGPA.isEmpty()) {
                    resultTV.setText("target GPA is Empty!");
                } else if(currentCredit.isEmpty()) {
                    resultTV.setText("current Credit is Empty!");
                } else if(additionalCredit.isEmpty()) {
                    resultTV.setText("additional Credit is Empty!");
                } else {
                    double cGPA = Double.parseDouble(currentGPA);
                    double tGPA = Double.parseDouble(targetGPA);
                    int cCredit = Integer.parseInt(currentCredit);
                    int aCredit = Integer.parseInt(additionalCredit);

                    double requiredGPA = ( (cCredit + aCredit) * tGPA - (cCredit * cGPA) ) / aCredit;

                    resultTV.setText("To achieve a target GPA of " + tGPA +
                            ", the GPA for the next " + aCredit +
                            " credits needs to be " + requiredGPA + " or higher.");
                }

            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                clearDisplay();
            }
        });


        return view;
    }

    public void clearDisplay() {
          currentGPAET.setText("");
          currentCreditET.setText("");
          targetGPAET.setText("");
          additionalCreditET.setText("");

          resultLayout.setVisibility(View.INVISIBLE);
    }
}