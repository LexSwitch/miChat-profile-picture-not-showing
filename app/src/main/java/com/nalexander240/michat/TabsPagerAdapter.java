package com.nalexander240.michat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class TabsPagerAdapter extends FragmentPagerAdapter{

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                requestsFragment RequestsFragment=new requestsFragment();
                return RequestsFragment;
            case 1:
                chatsFragment ChatsFragment=new chatsFragment();
                return ChatsFragment;
            case 2:
                friendsFragment FriendsRequest=new friendsFragment();
                return FriendsRequest;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){
        switch (position)
        {
            case 0:
                return  "REQUESTS";
            case 1:
                return "CHATS";
            case 2:
                return "FRIENDS";
            default:
                return null;
        }
    }
}
