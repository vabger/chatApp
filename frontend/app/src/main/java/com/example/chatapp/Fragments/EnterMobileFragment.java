package com.example.chatapp.Fragments;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.blongho.country_data.Country;
import com.blongho.country_data.World;
import com.example.chatapp.Adapters.ImageItem;
import com.example.chatapp.Adapters.ImageItemAdapter;
import com.example.chatapp.R;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.CheckedOutputStream;

public class EnterMobileFragment extends Fragment {
    Spinner countrySpinner;
    TextView isdTextView;
    EditText phoneEditText;
    GenerateOtpClickListener listener;
    Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();

    public interface GenerateOtpClickListener{
        void generateOtpClicked(View view);
    }

    public void setupCountrySpinner(){
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        World.init(getContext());

        List<ImageItem> countryItems = new ArrayList<>();
        List<String> regionCodes = new ArrayList<>();
        int defaultPos = 0;
        int i = -1;
        for(Country c: World.getAllCountries()){
            if(c.getName().equals("World"))continue;
            countryItems.add(new ImageItem(c.getName(),c.getFlagResource()));
            regionCodes.add(c.getAlpha2());
            i++;
            if(c.getName().equals("India")){
                defaultPos = i;
            }
        }


        countrySpinner.setAdapter(new ImageItemAdapter(getContext(),countryItems));
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int intCode = phoneNumberUtil.getCountryCodeForRegion(regionCodes.get(i));
                phoneNumber.setCountryCode(intCode);

                String isdCode = "+" + intCode;
                isdTextView.setText(isdCode);

                String exampleNumber = Long.toString(phoneNumberUtil.getExampleNumberForType(regionCodes.get(i), PhoneNumberUtil.PhoneNumberType.MOBILE).getNationalNumber());
                phoneEditText.setHint(exampleNumber);
                phoneEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(exampleNumber.length())});

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        countrySpinner.setSelection(defaultPos);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try{
            listener = (GenerateOtpClickListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context + " should implement GenerateOtpClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.enter_mobile_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        countrySpinner = view.findViewById(R.id.countrySpinner);
        isdTextView = view.findViewById(R.id.isdTextView);
        phoneEditText = view.findViewById(R.id.phoneEditText);

        setupCountrySpinner();

        Button generateOtpButton = view.findViewById(R.id.generateOtpButton);
        generateOtpButton.setOnClickListener(this::generateOtpClicked);
    }

    public Phonenumber.PhoneNumber getMobileNumber(){
        phoneNumber.setNationalNumber(Long.parseLong(phoneEditText.getText().toString()));
        return phoneNumber;
    }

    public void generateOtpClicked(View view){
        listener.generateOtpClicked(view);
    }
}
