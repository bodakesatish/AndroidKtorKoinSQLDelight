package com.bodakesatish.ktor

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bodakesatish.ktor.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    // Inject MainViewModel using Koin's delegate
    private val viewModel: MainViewModel by viewModel()
    private lateinit var mfSchemeAdapter: MFSchemeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        mfSchemeAdapter = MFSchemeAdapter(emptyList()) // Initialize with an empty list
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mfSchemeAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.mfSchemes.observe(this) { schemes ->
            mfSchemeAdapter.updateSchemes(schemes)
            binding.recyclerView.visibility = View.VISIBLE // Hide list when loading
            binding.errorTextView.visibility = View.GONE
            // Show list only if not loading and no error
                binding.recyclerView.visibility = if (schemes.isNotEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.recyclerView.visibility = View.GONE // Hide list when loading
                binding.errorTextView.visibility = View.GONE // Hide error when loading
            }
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                binding.errorTextView.text = errorMessage
                binding.errorTextView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE // Hide list on error
            } else {
                binding.errorTextView.visibility = View.GONE
            }
        }
    }
}