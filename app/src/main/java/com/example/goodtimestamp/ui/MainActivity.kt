package com.example.goodtimestamp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import com.example.goodtimestamp.GoodTimestampApplication
import com.example.goodtimestamp.R
import com.example.goodtimestamp.databinding.ActivityMainBinding
import com.example.goodtimestamp.ui.adapter.DiaryListAdapter
import com.example.goodtimestamp.viewmodel.DiaryViewModel
import com.example.goodtimestamp.viewmodel.DiaryViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: DiaryViewModel by viewModels {
        DiaryViewModelFactory((application as GoodTimestampApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFab()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.title_main)
        }
    }

    private fun setupRecyclerView() {
        val adapter = DiaryListAdapter { entry ->
            val intent = Intent(this, DiaryDetailActivity::class.java).apply {
                putExtra("diary_id", entry.id)
            }
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            viewModel.allEntries.collect { entries ->
                adapter.submitList(entries)
            }
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            val intent = Intent(this, DiaryEditActivity::class.java)
            startActivity(intent)
        }
    }
}