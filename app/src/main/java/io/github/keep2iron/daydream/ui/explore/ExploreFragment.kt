package io.github.keep2iron.daydream.ui.explore

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import io.github.keep2iron.daydream.R
import io.github.keep2iron.fast4android.arch.AbstractFragment

class ExploreFragment : AbstractFragment<ViewDataBinding>() {
  override fun initVariables(savedInstanceState: Bundle?) {
  }

  override fun resId(): Int = R.layout.explore_fragment
}