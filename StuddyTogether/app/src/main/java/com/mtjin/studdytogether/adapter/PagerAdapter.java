package com.mtjin.studdytogether.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mtjin.studdytogether.CityTabFragment;
import com.mtjin.studdytogether.StudyRoomTabFragment;
import com.mtjin.studdytogether.Tab3;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;


    public PagerAdapter(FragmentManager fm, int mNumOfTabs) {
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                CityTabFragment tab1 = new CityTabFragment();
                return tab1;
            case 1:
                StudyRoomTabFragment tab2 = new StudyRoomTabFragment();
                return tab2;
            case 2:
                Tab3 tab3 = new Tab3();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}