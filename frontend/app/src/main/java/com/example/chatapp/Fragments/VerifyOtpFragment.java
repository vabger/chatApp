package com.example.chatapp.Fragments;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatapp.R;

public class VerifyOtpFragment extends Fragment {

    VerifyOtpClickListener verifyOtpClickListener;
    ResendOtpClickListener resendOtpClickListener;
    CountDownTimer countDownTimer;
    EditText[] editTexts;

    public interface VerifyOtpClickListener{
        void verifyOtpClicked(View view);
    }

    public interface ResendOtpClickListener{
        void resendOtpClicked(View view);
    }

    static class GenericOnKeyListener implements EditText.OnKeyListener{
        EditText[] editTexts;
        int pos;

        public GenericOnKeyListener(EditText[] editTexts, int pos) {
            this.editTexts = editTexts;
            this.pos = pos;
        }

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if(keyEvent.getAction()==KeyEvent.ACTION_UP){
                if(editTexts[pos].length()==0&&i==KeyEvent.KEYCODE_DEL){
                    editTexts[pos].setText("");
                    if(pos>0){
                        editTexts[pos-1].requestFocus();
                    }
                    return true;
                }
                else if(editTexts[pos].length()==1&&pos+1<editTexts.length){
                    editTexts[pos+1].requestFocus();
                    return true;
                }
            }

            return false;
        }
    }

    public void resendOTP(View view){
        countDownTimer.start();
        resendOtpClickListener.resendOtpClicked(view);
    }

    public void verifyOTP(View view){
        verifyOtpClickListener.verifyOtpClicked(view);
    }

    public String getOtp(){
        StringBuilder otp = new StringBuilder();
        for (EditText editText : editTexts) {
            if (editText.length() == 0) {
                return null;
            }
            otp.append(editText.getText().toString());
        }
        return otp.toString();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            verifyOtpClickListener = (VerifyOtpClickListener) context;
            resendOtpClickListener = (ResendOtpClickListener) context;
        }
        catch (ClassCastException c){
            throw new ClassCastException(context + " should implement VerifyOtpClickListener and ResendOtpClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.verify_otp_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LinearLayout rootOtpLayout = view.findViewById(R.id.root_otp_layout);
        editTexts = new EditText[rootOtpLayout.getChildCount()];

        for(int i = 0;i<rootOtpLayout.getChildCount();i++){
            editTexts[i] = (EditText)rootOtpLayout.getChildAt(i);
        }

        for(int pos = 0;pos<rootOtpLayout.getChildCount();pos++){
            editTexts[pos].setOnKeyListener(new GenericOnKeyListener(editTexts, pos));
        }

        int maxTime = 120; //in seconds
        int interval = 1; //in seconds
        TextView timerTextView = view.findViewById(R.id.timer);

        countDownTimer = new CountDownTimer(maxTime*1000,interval*1000){
            @Override
            public void onTick(long l) {
                String text = "(" + l/1000 + ")";
                timerTextView.setText(text);
            }

            @Override
            public void onFinish() {
                timerTextView.setText(R.string.resend_otp);
                timerTextView.setPaintFlags(timerTextView.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                timerTextView.setOnClickListener(VerifyOtpFragment.this::resendOTP);
            }
        }.start();

        Button verifyOtpButton = view.findViewById(R.id.verifyotp);
        verifyOtpButton.setOnClickListener(this::verifyOTP);
    }

    @Override
    public void onDetach() {
        countDownTimer.cancel();
        super.onDetach();
    }
}


