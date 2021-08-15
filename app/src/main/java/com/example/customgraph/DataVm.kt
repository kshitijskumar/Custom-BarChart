package com.example.customgraph

import androidx.databinding.ObservableField

class DataVm {

    val expenses = ObservableField<List<Int>>(listOf())

    fun updateValues(points: List<Int>) {
        expenses.set(points)
    }
}