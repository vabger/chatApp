package com.example.chatapp.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatapp.API.API;
import com.example.chatapp.API.Auth.Phone;
import com.example.chatapp.API.User.PhoneNumbers;
import com.example.chatapp.API.User.User;
import com.example.chatapp.API.User.UserApi;
import com.example.chatapp.API.User.Users;
import com.example.chatapp.Activities.ChatActivity;
import com.example.chatapp.Adapters.UserAdapter;
import com.example.chatapp.R;
import com.example.chatapp.utils.FailedRequestHandler;
import com.example.chatapp.utils.ToastError;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsFragment extends Fragment {

    ToastError toastError;
    UserApi userApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toastError = new ToastError(getActivity());
        userApi = API.getUserApi();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }


    private void getUsers(View view,Map<String,String> phoneToLocalName){
        Call<Users> usersCall = userApi.getUsers(new PhoneNumbers(new ArrayList<>(phoneToLocalName.keySet())));

        usersCall.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if(!response.isSuccessful()){
                    new FailedRequestHandler(getActivity(), response.code(), response.errorBody());
                    return;
                }
                Map<String, User> phoneToUser = new HashMap<>();
                ArrayList<User> users = response.body().getUsers();
                for(User u:users){
                    phoneToUser.put(u.getPhone(),u);
                }
                setupContactsUI(view,phoneToUser,phoneToLocalName);
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                toastError.showMessage("Unable to get users list! Try again later");
            }
        });
    }

    private void setupContactsUI(View view,Map<String,User>phoneToUser,Map<String,String>phoneToLocalName){
        List<User> exists = new ArrayList<>();
        List<String> phone = new ArrayList<>();
        List<String> name = new ArrayList<>();

        for(String p:phoneToLocalName.keySet()){
            if(phoneToUser.containsKey(p)){
                exists.add(phoneToUser.get(p));
            }
            else{
                phone.add(p);
                name.add(phoneToLocalName.get(p));
            }
        }

        Log.i("phone",phone.toString());

        RecyclerView recyclerView = view.findViewById(R.id.contactsRecyclerView);
        recyclerView.setAdapter(new UserAdapter(exists,phone,name));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if(result){
                if(getActivity()!=null){
                    Map<String,String> phoneToLocalName = new HashMap<>();
                    phoneToLocalName = getContacts(getActivity());
                    getUsers(view,phoneToLocalName);
                }
            }
            else{
                toastError.showMessage("Contacts permission needed to access this page!");
                getActivity().getFragmentManager().popBackStack();
            }
        });

        activityResultLauncher.launch(Manifest.permission.READ_CONTACTS);

    }

    @SuppressLint("Range")
    public Map<String,String> getContacts(@NonNull Context ctx) {
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        Map<String,String> map = new HashMap<>();
        ContentResolver contentResolver = ctx.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (cursorInfo.moveToNext()) {
                        String phone = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        try {
                            Phonenumber.PhoneNumber phoneNumber = util.parse(phone,"in");
                            map.put(util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164),cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                        } catch (NumberParseException e) {
                            e.printStackTrace();
                        }

                    }

                    cursorInfo.close();
                }
            }
            cursor.close();
        }
        return map;
    }
}