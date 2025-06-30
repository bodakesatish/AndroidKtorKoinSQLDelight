package com.bodakesatish.ktor

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bodakesatish.ktor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var mfSchemeAdapter: MFSchemeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

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
            binding.recyclerView.visibility = if (schemes.isNotEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE // Hide list when loading
            binding.errorTextView.visibility = View.GONE // Hide error when loading
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                binding.errorTextView.text = errorMessage
                binding.errorTextView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE // Hide list on error
            } else {
                binding.errorTextView.visibility = View.GONE
            }
        }
    }
}