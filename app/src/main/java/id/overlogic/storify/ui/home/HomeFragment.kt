package id.overlogic.storify.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.overlogic.storify.MainViewModel
import id.overlogic.storify.databinding.FragmentHomeBinding
import id.overlogic.storify.ui.common.factory.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var _viewModel: HomeViewModel? = null
    private val viewModel get() = _viewModel!!

    private var _mainViewModel: MainViewModel? = null
    private val mainViewModel get() = _mainViewModel!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        _viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        _mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mainViewModel.getSession().observe(viewLifecycleOwner) { user ->
            binding.tvUsername.text = user.name
        }
        getData()
    }

    private fun getData() {
        binding.pbHome.visibility = View.GONE
        val storyAdapter = StoryAdapter()
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
        }
        viewModel.result.observe(viewLifecycleOwner) {
            storyAdapter.submitData(lifecycle, it)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _viewModel = null
        _mainViewModel = null
    }
}