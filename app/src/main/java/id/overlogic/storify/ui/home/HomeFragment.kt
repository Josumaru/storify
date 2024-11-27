package id.overlogic.storify.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.overlogic.storify.MainViewModel
import id.overlogic.storify.databinding.FragmentHomeBinding
import id.overlogic.storify.ui.auth.login.LoginActivity
import id.overlogic.storify.ui.common.factory.ViewModelFactory
import id.overlogic.storify.util.SpacesItemDecoration

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView andRegisterViewModel
    // onDestroyView.
    private val binding get() = _binding!!

    private var _viewModel: HomeViewModel? = null
    private val viewModel get() = _viewModel!!

    private var _mainViewModel: MainViewModel? = null
    private val mainViewModel get() = _mainViewModel!!

    private var isAlertShown = false

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
        binding.srlHome.setOnRefreshListener {
            viewModel.refreshStories()
            binding.pbHome.visibility = View.VISIBLE
        }
        viewModel.error.observe(viewLifecycleOwner) { result ->
            if (!isAlertShown && result != null) {
                isAlertShown = true
                AlertDialog.Builder(requireActivity())
                    .setTitle("Error")
                    .setMessage(result)
                    .setPositiveButton("OK") { dialog, _ ->
                        binding.pbHome.visibility = View.GONE
                        isAlertShown = false
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }
        viewModel.stories.observe(viewLifecycleOwner) { result ->
            val storyAdapter = StoryAdapter()
            storyAdapter.submitList(result)
            binding.rvStories.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = storyAdapter
            }
            binding.srlHome.isRefreshing = false
            binding.pbHome.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}