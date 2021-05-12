package com.simpleplus.timecounter.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.adapter.DateFilterAdapter

class MyItemDecoration(
    private val context: Context,
    private val screenWidth: Int,
    private val viewWidth: Int
) : RecyclerView.ItemDecoration() {

    private val dataSize = DateFilterAdapter.CONST_ITEMS_COUNT
    private val layoutPadding = context.resources.getDimensionPixelSize(R.dimen.margin_16dp) + context.resources.getDimensionPixelSize(R.dimen.margin_3dp)
    private val startEndOffSet = (screenWidth/2 - layoutPadding ) - (viewWidth / 2)


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)

        if (position == dataSize) {
            outRect.set(0, 0, startEndOffSet, 0)
        } else if (position == 0) {
            outRect.set(startEndOffSet, 0, 0, 0)
        } else {
            outRect.set(0, 0, 0, 0)
        }

    }
}