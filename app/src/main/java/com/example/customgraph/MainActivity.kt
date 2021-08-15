package com.example.customgraph

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.customgraph.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val data = DataVm()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.vm = data
        setContentView(binding.root)

        val expenses = listOf(500, 100, 600, 320, 600, 500, 400)
        data.updateValues(expenses)

        binding.barChart.setOnClickListener {
            data.updateValues(List(7) { Random.nextInt(0, 1000) })
        }

    }
}