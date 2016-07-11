package com.ziv.trendsviewpager;

import java.util.ArrayList;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends FragmentActivity implements TextProvider {

	private Button mAdd;
	private Button mRemove;
	private ViewPager mPager;

	private MyPagerAdapter mAdapter;

	private ArrayList<String> mEntries = new ArrayList<>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initList();
		findById();
		init();

		mAdapter = new MyPagerAdapter(this.getSupportFragmentManager(), this);
		mPager.setAdapter(mAdapter);

	}

	private void init() {
		mAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				addNewItem();
			}
		});

		mRemove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				removeCurrentItem();
			}
		});

	}

	private void findById() {
		mAdd = (Button) findViewById(R.id.add);
		mRemove = (Button) findViewById(R.id.remove);
		mPager = (ViewPager) findViewById(R.id.pager);
	}

	private void initList() {
		mEntries.add("第1页");
		mEntries.add("第2页");
		mEntries.add("第3页");
		mEntries.add("第4页");
		mEntries.add("第5页");

	}


	private void addNewItem() {
		mEntries.add("new item");
		mAdapter.notifyDataSetChanged();
		mPager.setCurrentItem(mEntries.size());
	}

	private void removeCurrentItem() {
		int position = mPager.getCurrentItem();
		if (getCount() > 0) {
			mEntries.remove(position);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public String getTextForPosition(int position) {
		return mEntries.get(position);
	}

	@Override
	public int getCount() {
		return mEntries.size();
	}

}