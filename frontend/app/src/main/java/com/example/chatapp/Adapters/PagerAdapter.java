package com.example.chatapp.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.chatapp.Fragments.ChatsFragment;
import com.example.chatapp.Fragments.ContactsFragment;
import com.example.chatapp.Fragments.ProfileFragment;

import java.util.List;

public class PagerAdapter extends FragmentStateAdapter {
    private Context myContext;
    List<Fragment> fragmentList;

    public PagerAdapter(Context context, FragmentManager fragmentManager, Lifecycle lifecycle, List<Fragment> fragmentList) {
        super(fragmentManager,lifecycle);
        myContext = context;
        this.fragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
