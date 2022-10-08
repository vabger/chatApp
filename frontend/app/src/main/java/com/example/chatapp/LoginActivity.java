package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.chatapp.API.API;
import com.example.chatapp.API.Auth.AuthApi;
import com.example.chatapp.API.Auth.MobileNumber;
import com.example.chatapp.API.Auth.OtpHash;
import com.example.chatapp.API.Auth.OtpVerificationBody;
import com.example.chatapp.API.Auth.Token;
import com.example.chatapp.Config.RetrofitClient;
import com.example.chatapp.Fragments.EnterMobileFragment;
import com.example.chatapp.Fragments.VerifyOtpFragment;
import com.example.chatapp.utils.ToastError;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements EnterMobileFragment.GenerateOtpClickListener, VerifyOtpFragment.VerifyOtpClickListener, VerifyOtpFragment.ResendOtpClickListener {
    EnterMobileFragment enterMobileFragment = new EnterMobileFragment();
    VerifyOtpFragment verifyOtpFragment = new VerifyOtpFragment();
    FragmentManager fragmentManager;

    ToastError toastError = new ToastError(this);

    AuthApi authApi;
    OtpHash otpHash;
    String formattedNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.loginfl,enterMobileFragment).commit();

        authApi = API.getAuthApi();
    }

    @Override
    public void generateOtpClicked(View view) {
        //Number Validation
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = enterMobileFragment.getMobileNumber();

        if(!phoneNumberUtil.isValidNumber(phoneNumber)){
            toastError.showMessage("Enter a valid mobile number!");
            return;
        }
        formattedNumber = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);

        //Sending the request for OTP
        Call<OtpHash> call = authApi.getOtp(new MobileNumber(formattedNumber));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<OtpHash> call, @NonNull Response<OtpHash> response) {
                if (!response.isSuccessful() && response.errorBody() != null) {
                    toastError.showMessage(response.errorBody());
                    return;
                }
                otpHash = response.body(); //Storing the otp hash for future verification

                Toast.makeText(LoginActivity.this,"OTP sent!",Toast.LENGTH_SHORT).show();
                //Going to verification page
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.loginfl, verifyOtpFragment).commit();
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            }

            @Override
            public void onFailure(@NonNull Call<OtpHash> call, @NonNull Throwable t) {
                Log.i("error",t.getMessage());
                toastError.showMessage("Network Error!");
            }
        });


    }

    @Override
    public void verifyOtpClicked(View view) {
        String otp = verifyOtpFragment.getOtp();
        if(otp==null){
            toastError.showMessage("Invalid OTP!");
            return;
        }

        OtpVerificationBody otpVerificationBody = new OtpVerificationBody(otpHash.getHash(),formattedNumber,otp);
        Log.i("otp",otpVerificationBody.toString());
        Call<Token> call = authApi.verifyOtp(otpVerificationBody);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (!response.isSuccessful() && response.errorBody() != null) {
                    toastError.showMessage(response.errorBody());
                    return;
                }
                assert response.body() != null;
                Log.i("token",response.body().toString());

                Intent intent = new Intent(LoginActivity.this,ChatActivity.class);
                LoginActivity.this.finish();
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                toastError.showMessage("Invalid OTP!");
            }
        });
    }

    @Override
    public void resendOtpClicked(View view) {
        generateOtpClicked(view);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.loginfl, enterMobileFragment).commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}