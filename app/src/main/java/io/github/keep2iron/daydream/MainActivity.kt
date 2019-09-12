package io.github.keep2iron.daydream

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import io.github.keep2iron.fast4android.arch.AbstractActivity
import io.github.keep2iron.fast4android.arch.FindViewById
import io.github.keep2iron.fast4android.tabsegment.FastTabSegmentLayout
import io.github.keep2iron.fast4android.tabsegment.TextFastTabSegmentAdapter

class MainActivity : AbstractActivity<ViewDataBinding>() {
  private val viewPager: ViewPager by FindViewById(R.id.viewPager)
  private val tabSegmentLayout: FastTabSegmentLayout by FindViewById(R.id.tabSegmentLayout)
  override fun initVariables(savedInstanceState: Bundle?) {
    val tabs = arrayListOf(
      "我的",
      "探索",
      "悟"
    )
    viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
      override fun getItem(position: Int): Fragment = Fragment()
      override fun getCount(): Int = tabs.size
    }
    tabSegmentLayout.tabMode = FastTabSegmentLayout.MODE_FIXED
    val tabSegmentAdapter = object : TextFastTabSegmentAdapter(tabs) {
      override fun onBindTab(view: View, index: Int, selected: Boolean) {
        super.onBindTab(view, index, selected)
        if (selected) {
          (view as TextView).textSize = 18f
        } else {
          (view as TextView).textSize = 16f
        }
      }
    }
    tabSegmentLayout.setupWithViewPager(viewPager,1)
    tabSegmentLayout.setAdapter(tabSegmentAdapter)
  }

  override fun resId(): Int = R.layout.activity_main
}