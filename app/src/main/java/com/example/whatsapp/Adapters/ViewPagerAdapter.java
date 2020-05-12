package com.example.whatsapp.Adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.whatsapp.Fragments.Calls;
import com.example.whatsapp.Fragments.Chats;
import com.example.whatsapp.Fragments.Contacts;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] fragments;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new Fragment[] {
            new Chats(),
            new Calls(),
            new Contacts()
        };

    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = getItem(position).getClass().getName();
        return title.subSequence(title.lastIndexOf("."), title.length());
    }
}
