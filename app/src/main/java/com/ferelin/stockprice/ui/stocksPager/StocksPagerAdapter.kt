package com.ferelin.stockprice.ui.stocksPager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ferelin.stockprice.ui.favourite.FavouriteFragment
import com.ferelin.stockprice.ui.stocks.StocksFragment

class StocksPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StocksFragment()
            1 -> FavouriteFragment()
            else -> throw IllegalStateException("No fragment for position: $position")
        }
    }
}