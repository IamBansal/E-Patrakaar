package com.example.e_patrakaar.view

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * Class for wrapping the StaggeredGridLayoutManager
 * It is required for solving the error of 'Inconsistency detected'
 * For more info: https://stackoverflow.com/a/33822747/13733026
 */
class WrapContentStaggeredGridLayoutManager(count: Int, orientation: Int) :
    StaggeredGridLayoutManager(count, orientation) {
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Log.e("TAG", "indexOutOfBoundsExceptionOccurred")
        }
    }
}