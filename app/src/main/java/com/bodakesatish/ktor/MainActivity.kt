package com.bodakesatish.ktor

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bodakesatish.ktor.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
        setupSwipeToRefresh()
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshSchemes()
        }
    }

    private fun setupRecyclerView() {
        mfSchemeAdapter = MFSchemeAdapter(emptyList()) // Initialize with an empty list
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mfSchemeAdapter
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                binding.progressBar.visibility =
                    if (state.isLoading && !binding.swipeRefreshLayout.isRefreshing) View.VISIBLE else View.GONE
                binding.swipeRefreshLayout.isRefreshing =
                    state.isLoading // Manage SwipeRefresh animation

                if (state.errorMessage != null) {
                    binding.errorTextView.text = state.errorMessage
                    binding.errorTextView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.errorTextView.visibility = View.GONE
                }

                if (!state.isLoading && state.errorMessage == null) {
                    mfSchemeAdapter.updateSchemes(state.schemes)
                    binding.recyclerView.visibility =
                        if (state.schemes.isNotEmpty()) View.VISIBLE else View.GONE
                    // If schemes are empty and no error, you might want to show a "No data" message
                    // binding.emptyView.visibility = if (state.schemes.isEmpty()) View.VISIBLE else View.GONE
                } else if (state.isLoading || state.errorMessage != null) {
                    binding.recyclerView.visibility = View.GONE
                }

                // Ensure SwipeRefreshLayout stops refreshing when loading is complete,
                // regardless of whether it was triggered by swipe or initial load.
                if (!state.isLoading && binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }
}