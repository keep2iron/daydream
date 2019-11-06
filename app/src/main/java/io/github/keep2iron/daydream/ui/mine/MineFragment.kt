package io.github.keep2iron.daydream.ui.mine

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import io.github.keep2iron.daydream.R
import io.github.keep2iron.fast4android.arch.AbstractFragment

class MineFragment : AbstractFragment<ViewDataBinding>() {

    override fun initVariables(savedInstanceState: Bundle?) {
    }

    override fun resId(): Int = R.layout.mine_fragment

}