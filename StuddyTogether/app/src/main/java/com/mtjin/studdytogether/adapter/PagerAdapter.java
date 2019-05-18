package com.mtjin.studdytogether.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mtjin.studdytogether.fragment.CityTabFragment;
import com.mtjin.studdytogether.fragment.FreeBoardTabFragment;
import com.mtjin.studdytogether.fragment.IndividualTabFragment;

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
                IndividualTabFragment tab2 = new IndividualTabFragment();
                return tab2;
            case 2:
                FreeBoardTabFragment freeBoardTabFragment = new FreeBoardTabFragment();
                return freeBoardTabFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
