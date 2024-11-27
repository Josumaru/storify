package id.overlogic.storify.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import id.overlogic.storify.MainActivity
import id.overlogic.storify.MainViewModel
import id.overlogic.storify.R
import id.overlogic.storify.databinding.FragmentHomeBinding
import id.overlogic.storify.databinding.FragmentProfileBinding
import id.overlogic.storify.ui.common.factory.ViewModelFactory
import id.overlogic.storify.ui.home.HomeViewModel

class ProfileFragment : Fragment() {

    private var _mainViewModel: MainViewModel? = null
    private val mainViewModel get() = _mainViewModel!!

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        mainViewModel.getSession().observe(viewLifecycleOwner) { user ->
            binding.tvName.text = user.name
            binding.tvUserId.text = user.userId
        }

        binding.actionLogout.setOnClickListener {
            mainViewModel.destroySession()
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }


    }
}