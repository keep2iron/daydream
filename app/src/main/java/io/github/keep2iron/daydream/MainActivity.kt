package io.github.keep2iron.daydream

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import io.github.keep2iron.base.util.setPaddingTop
import io.github.keep2iron.base.util.setStatusBarDark
import io.github.keep2iron.daydream.ui.explore.ExploreFragment
import io.github.keep2iron.fast4android.arch.AbstractActivity
import io.github.keep2iron.fast4android.arch.util.findViewByDelegate
import io.github.keep2iron.fast4android.tabsegment.FastTabSegmentLayout
import io.github.keep2iron.fast4android.tabsegment.TextFastTabSegmentAdapter
import kotlin.math.ceil

class MainActivity : AbstractActivity<ViewDataBinding>() {
    private val viewPager: ViewPager by findViewByDelegate(R.id.viewPager)

    private val tabContainer: FrameLayout by findViewByDelegate(R.id.tabContainer)

    private val tabSegmentLayout: FastTabSegmentLayout by findViewByDelegate(R.id.tabSegmentLayout)

    override fun initVariables(savedInstanceState: Bundle?) {
        setStatusBarDark()
        initViewPager()

        ViewCompat.setOnApplyWindowInsetsListener(tabContainer) { v, insets ->
            v.setPaddingTop(insets.systemWindowInsetTop)
            insets
        }
    }

    private fun initViewPager() {
        val tabs = arrayListOf(
            "我的",
            "探索",
            "悟"
        )
        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment = ExploreFragment()
            override fun getCount(): Int = tabs.size
        }
        viewPager.offscreenPageLimit = tabs.size

        val tabSegmentAdapter = MainTextTabAdapter(tabSegmentLayout, tabs)
        tabSegmentLayout.setupWithViewPager(viewPager, 1)
        tabSegmentLayout.setAdapter(tabSegmentAdapter)

        viewPager.addOnPageChangeListener(tabSegmentAdapter)
    }

    override fun resId(): Int = R.layout.activity_main
}

class MainTextTabAdapter(
    private val tabSegmentLayout: FastTabSegmentLayout,
    data: List<String>
) :
    TextFastTabSegmentAdapter(data), ViewPager.OnPageChangeListener {
    var pageSelectPosition = 0
    var positionOffset = 0.0f

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (positionOffset == 0.0f || positionOffset == 1.0f) {
            var lastPosition = this.pageSelectPosition
            this.pageSelectPosition = position
            this.positionOffset = positionOffset

            for (i in 0 until getItemSize()) {
                val tab = tabSegmentLayout.childTabAt(i) as TextView
                if (i == pageSelectPosition) {
                    tab.textSize = 18f
                } else {
                    tab.textSize = 14f
                }
            }

            (tabSegmentLayout.childTabAt(lastPosition) as TextView).textSize = 14f
            (tabSegmentLayout.childTabAt(this.pageSelectPosition) as TextView).textSize = 18f

            return
        }

        if (pageSelectPosition > position + positionOffset) {
            val curPos = pageSelectPosition
            val nextPos = (position + positionOffset).toInt()
            (tabSegmentLayout.childTabAt(curPos) as TextView).textSize =
                14 + positionOffset * 4
            (tabSegmentLayout.childTabAt(nextPos) as TextView).textSize =
                14 + (1 - positionOffset) * 4
        } else {
            val curPos = pageSelectPosition
            val nextPos = ceil((position + positionOffset).toDouble()).toInt()
            (tabSegmentLayout.childTabAt(curPos) as TextView).textSize =
                14 + (1 - positionOffset) * 4
            (tabSegmentLayout.childTabAt(nextPos) as TextView).textSize =
                14 + positionOffset * 4
        }
    }

    override fun onPageSelected(position: Int) {
    }

}