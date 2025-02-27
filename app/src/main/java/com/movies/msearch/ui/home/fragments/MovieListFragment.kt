package com.movies.msearch.ui.home.fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.movies.msearch.R
import com.movies.msearch.application.MoviesApp
import com.movies.msearch.data.network.ApiResponse
import com.movies.msearch.databinding.FragmentMovieListBinding
import com.movies.msearch.ui.adapters.MovieAdapter
import com.movies.msearch.ui.home.viewmodel.MovieViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieListFragment : Fragment() {

    private lateinit var viewModel: MovieViewModel
    private lateinit var adapter: MovieAdapter
    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!
    private var isSearchEnabled = false

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // Do nothing (disable the back button)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity().application as MoviesApp).appComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MovieViewModel::class.java)
        adapter = MovieAdapter()
        binding.movieRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.movieRecyclerView.adapter = adapter
        observeMovies()
        binding.searchBtn.isEnabled = false
        triggerSearch()
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        changeButtonState()
    }

    private fun triggerSearch() {
        binding.searchBtn.apply {
            setOnClickListener {
                viewModel.searchMovies(binding.editTextSearch.text.toString())
                hideKeyboard()
            }
        }
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextSearch.windowToken, 0)
    }

    private fun observeMovies() {
        lifecycleScope.launch {
            viewModel.moviesState.collect { state ->
                when (state) {
                    is ApiResponse.Loading -> {
                        binding.statusUpdateView.visibility = View.VISIBLE
                        binding.statusUpdateView.text = getString(R.string.loading_msg)
                    }

                    is ApiResponse.Success -> {
                        if (!state.data.search.isNullOrEmpty()) {
                            binding.statusUpdateView.visibility = View.GONE
                            binding.movieRecyclerView.visibility = View.VISIBLE
                            adapter.submitList(state.data.search)
                        } else {
                            binding.statusUpdateView.visibility = View.VISIBLE
                            binding.statusUpdateView.text = getString(R.string.empty_list)
                            adapter.submitList(emptyList())
                            showToast(getString(R.string.empty_list), Toast.LENGTH_LONG)
                        }
                    }

                    is ApiResponse.Error -> {
                        binding.statusUpdateView.visibility = View.VISIBLE
                        binding.statusUpdateView.text = state.message
                        showToast(state.message, Toast.LENGTH_SHORT)
                    }
                }
            }
        }
    }

    private fun showToast(message: String, length: Int) {
        Toast.makeText(requireContext(), message, length).show()
    }

    private fun changeButtonState() {
        binding.editTextSearch.apply {
            imeOptions = EditorInfo.IME_ACTION_NONE
            setOnEditorActionListener { _, actionId, event ->
                if (isSearchEnabled && actionId == EditorInfo.IME_ACTION_SEARCH
                ) {
                    viewModel.searchMovies(text.toString())
                    hideKeyboard()
                    true
                } else {
                    showToast(getString(R.string.enter_search_text), Toast.LENGTH_LONG)
                    binding.movieRecyclerView.visibility = View.GONE
                    false
                }
            }

            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    var hasInput: Boolean
                    if (s?.contains("\n") == true) {
                        val filteredText = s.toString().replace("\n", "")
                        setText(filteredText)
                        setSelection(filteredText.length)
                        hasInput = filteredText.isNotEmpty()
                    } else {
                        hasInput = text.isNotEmpty()
                    }
                    binding.searchBtn.isEnabled = hasInput
                    imeOptions = if (hasInput) {
                        EditorInfo.IME_ACTION_SEARCH
                    } else {
                        binding.statusUpdateView.text = getString(R.string.no_movies_to_show_msg)
                        EditorInfo.IME_ACTION_NONE
                    }
                    setInputType(inputType)
                    isSearchEnabled = hasInput
                    binding.searchBtn.isEnabled = hasInput
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.remove()
    }
}