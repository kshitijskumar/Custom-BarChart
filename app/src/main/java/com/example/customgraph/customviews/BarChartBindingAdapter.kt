package com.example.customgraph.customviews

import androidx.databinding.BindingAdapter

class BarChartBindingAdapter {

    companion object {

        @JvmStatic
        @BindingAdapter("app:barPoints")
        fun setNewBarPoints(bc: BarChart, pointsList: List<Int>) {
            bc.setBarPoints(pointsList)
        }
    }

}