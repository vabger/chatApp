package com.example.chatapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatapp.API.API;
import com.example.chatapp.API.User.User;
import com.example.chatapp.API.User.UserApi;
import com.example.chatapp.R;
import com.example.chatapp.Session;
import com.example.chatapp.utils.FailedRequestHandler;
import com.example.chatapp.utils.ToastError;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    UserApi userApi;
    ToastError toastError;

    private void updateProfileCLicked(View view){
        EditText username = requireActivity().findViewById(R.id.usernameEditText);
        Call<User> call = API.getUserApi().updateUsername(username.getText().toString());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getContext(), "Username updated!", Toast.LENGTH_SHORT).show();
                    Session.getInstance().setCurrentUser(response.body());
                }
                else{
                    new FailedRequestHandler(getContext(),response.code(),response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userApi = API.getUserApi();
        toastError = new ToastError(getContext());

        Call<User> call = userApi.getCurrentUser();

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    new FailedRequestHandler(getContext(), response.code(), response.errorBody()).handle();
                    return;
                }

                User currentUser = response.body();
                if(currentUser!=null){
                    EditText username = view.findViewById(R.id.usernameEditText);
                    ImageView avatar = view.findViewById(R.id.profileAvatarImageView);
                    username.setText(currentUser.getUsername());
                    Picasso.get().load(currentUser.getAvatar()).into(avatar);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                toastError.showMessage("Something went wrong! Check your network");
            }
        });

        Button update = view.findViewById(R.id.updateProfileButton);
        update.setOnClickListener(this::updateProfileCLicked);


    }

}