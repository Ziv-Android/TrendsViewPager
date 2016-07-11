package com.ziv.trendsviewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

	private TextProvider mProvider;

	public MyPagerAdapter(FragmentManager fm, TextProvider provider) {
		super(fm);
		this.mProvider = provider;
	}

	@Override
	public Fragment getItem(int position) {
		return MyFragment.newInstance(mProvider
				.getTextForPosition(position));
	}

	@Override
	public int getCount() {
		return mProvider.getCount();
	}

	// this is called when notifyDataSetChanged() is called
	@Override
	public int getItemPosition(Object object) {
		// refresh all fragments when data set changed
		return PagerAdapter.POSITION_NONE;
	}

}