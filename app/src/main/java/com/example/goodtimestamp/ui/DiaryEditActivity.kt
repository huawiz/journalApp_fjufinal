package com.example.goodtimestamp.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.goodtimestamp.GoodTimestampApplication
import com.example.goodtimestamp.R
import com.example.goodtimestamp.databinding.ActivityDiaryEditBinding
import com.example.goodtimestamp.viewmodel.DiaryViewModel
import com.example.goodtimestamp.viewmodel.DiaryViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class DiaryEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryEditBinding
    private val viewModel: DiaryViewModel by viewModels {
        DiaryViewModelFactory((application as GoodTimestampApplication).repository)
    }
    private var diaryId: Long = -1
    private var originalTitle = ""
    private var originalContent = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        diaryId = intent.getLongExtra("diary_id", -1)
        Log.d("DiaryEditActivity", "Received diary_id: $diaryId") // 添加日誌

        setupToolbar()
        if (diaryId != -1L) {
            loadDiary()
        }
        setupSaveButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            val titleRes = if (diaryId == -1L) R.string.title_new else R.string.title_edit
            title = getString(titleRes)
            Log.d("DiaryEditActivity", "Setting title with diaryId: $diaryId") // 添加日誌
        }
    }

    private fun loadDiary() {
        lifecycleScope.launch {
            viewModel.loadEntry(diaryId)
            viewModel.currentEntry.collect { entry ->
                entry?.let {
                    binding.editTitle.setText(it.title)
                    binding.editContent.setText(it.content)
                    binding.sliderIntensity.value = it.intensity.toFloat()
                    binding.sliderEnergy.value = it.energy.toFloat()
                    originalTitle = it.title
                    originalContent = it.content
                }
            }
        }
    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            saveAndFinish()
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        val currentTitle = binding.editTitle.text.toString()
        val currentContent = binding.editContent.text.toString()

        return if (diaryId == -1L) {
            currentTitle.isNotBlank() || currentContent.isNotBlank()
        } else {
            currentTitle != originalTitle || currentContent != originalContent
        }
    }

    override fun onBackPressed() {
        if (hasUnsavedChanges()) {
            showUnsavedChangesDialog()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (hasUnsavedChanges()) {
            showUnsavedChangesDialog()
        } else {
            finish()
        }
        return true
    }

    private fun showUnsavedChangesDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_unsaved_title)
            .setMessage(R.string.dialog_unsaved_message)
            .setPositiveButton(R.string.action_save) { _, _ ->
                saveAndFinish()
            }
            .setNegativeButton(R.string.action_discard) { _, _ ->
                finish()
            }
            .setNeutralButton(R.string.action_cancel, null)
            .show()
    }

    private fun saveAndFinish() {
        val title = binding.editTitle.text.toString()
        val content = binding.editContent.text.toString()
        val intensity = binding.sliderIntensity.value.toInt()
        val energy = binding.sliderEnergy.value.toInt()

        if (title.isBlank() || content.isBlank()) {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            if (diaryId == -1L) {
                viewModel.insertEntry(title, content, intensity, energy)
            } else {
                viewModel.currentEntry.value?.let { entry ->
                    val updatedEntry = entry.copy(
                        title = title,
                        content = content,
                        intensity = intensity,
                        energy = energy,
                        modifiedDate = System.currentTimeMillis()
                    )
                    viewModel.updateEntry(updatedEntry)
                }
            }
            finish()
        }
    }
}