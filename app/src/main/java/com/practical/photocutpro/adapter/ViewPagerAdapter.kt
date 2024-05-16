package com.practical.photocutpro.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager 
import androidx.fragment.app.FragmentStatePagerAdapter 

class ViewPagerAdapter(supportFragmentManager: FragmentManager) : 
	FragmentStatePagerAdapter(supportFragmentManager) { 

	private val mFragmentList = ArrayList<Fragment>()
	private val mFragmentTitleList = ArrayList<String>() 

	override fun getItem(position: Int): Fragment { 
		return mFragmentList[position]
	} 

	override fun getCount(): Int { 
		return mFragmentList.size
	} 

	override fun getPageTitle(position: Int): CharSequence{ 
		return mFragmentTitleList[position]
	} 

	fun addFragment(fragment: Fragment, title: String) { 
		mFragmentList.add(fragment)
		mFragmentTitleList.add(title) 
	}

	fun getFragment(position: Int): Fragment? {
		return if (position >= 0 && position < mFragmentList.size) {
			mFragmentList[position]
		} else {
			null
		}
	}

}
