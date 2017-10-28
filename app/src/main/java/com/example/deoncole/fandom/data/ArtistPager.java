package com.example.deoncole.fandom.data;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.deoncole.fandom.model.Artist;
import com.example.deoncole.fandom.ui.fragment.MusicFragment;
import com.example.deoncole.fandom.ui.fragment.PostFragment;

public class ArtistPager extends FragmentPagerAdapter {

    private int tabCount;
    private Artist mArtist;

    public ArtistPager(FragmentManager manager, int tabCount, final Artist artist){
        super(manager);
        this.tabCount = tabCount;
        mArtist = artist;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return PostFragment.newInstance(mArtist);
            case 1:
                return new MusicFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
