package com.example.chatapp.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatapp.API.API;
import com.example.chatapp.API.User.User;
import com.example.chatapp.API.User.UserApi;
import com.example.chatapp.Adapters.PagerAdapter;
import com.example.chatapp.Fragments.ChatsFragment;
import com.example.chatapp.Fragments.ContactsFragment;
import com.example.chatapp.Fragments.ProfileFragment;
import com.example.chatapp.R;
import com.example.chatapp.utils.FailedRequestHandler;
import com.example.chatapp.utils.ToastError;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    UserApi userApi;
    ToastError toastError;

    ChatsFragment chatsFragment;
    ContactsFragment contactsFragment;
    ProfileFragment profileFragment;

    void setupTabLayout(List<Fragment> fragmentList){
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager2  = findViewById(R.id.viewPager);
        viewPager2.setAdapter(new PagerAdapter(this,getSupportFragmentManager(),getLifecycle(),fragmentList));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
//
//        toastError = new ToastError(this);

        chatsFragment = new ChatsFragment();
        profileFragment = new ProfileFragment();
        contactsFragment = new ContactsFragment();

        List<Fragment> fragmentsList = new ArrayList<>();
        fragmentsList.add(chatsFragment);
        fragmentsList.add(contactsFragment);
        fragmentsList.add(profileFragment);

        setupTabLayout(fragmentsList);


    }

}