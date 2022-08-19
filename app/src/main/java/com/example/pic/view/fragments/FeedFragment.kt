package com.example.pic.view.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pic.R
import com.example.pic.databinding.FragmentHomeBinding
import com.example.pic.util.loadImage
import com.example.pic.util.showSnackBar
import com.example.pic.view.adapter.FeedPagerDataAdapter
import com.example.pic.view.custom.gone
import com.example.pic.view.custom.imgPreview
import com.example.pic.view.custom.visible
import com.example.pic.viewModel.FeedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
@OptIn(ExperimentalPagingApi::class)
class FeedFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private val viewModel: FeedViewModel by viewModels()
    private lateinit var feedAdapter: FeedPagerDataAdapter
    val bundle = Bundle()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        init()


        viewModel.getImages().observe(viewLifecycleOwner){
            lifecycleScope.launch(Dispatchers.IO){

                feedAdapter.submitData(it)
                runBlocking {

                }
            }

        }

        lifecycleScope.launch(viewLifecycleOwner.lifecycleScope.coroutineContext) {
            feedAdapter.loadStateFlow.collectLatest { loadStates ->
                if (loadStates.refresh is LoadState.Loading){
                    binding.shimmerViewContainer.startShimmerAnimation()
                    binding.shimmerViewContainer.visible()
                }else{
                    binding.shimmerViewContainer.stopShimmerAnimation()
                    binding.shimmerViewContainer.gone()
                }

                if (loadStates.append is LoadState.Error){
                    binding.root.showSnackBar("Error", "Check your Internet and try again!")
                }
            }
        }


        return binding.root
    }

    private fun init(){
        setUpList()
    }

    private fun setUpList(){
        feedAdapter = FeedPagerDataAdapter(){ feed, onLong ->
            if (onLong){
                binding.root.imgPreview(feed?.urls?.regular)
            }else {
//                imageID = feed.id
                bundle.putString("imageID", feed?.id)
                findNavController().navigate(R.id.detailsFeedFragment, bundle)
            }
        }
        val gridLayoutManager = GridLayoutManager(requireContext(),2)

        binding.feedRecv.apply {
            layoutManager = gridLayoutManager
            adapter = feedAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        binding.shimmerViewContainer.startShimmerAnimation()
    }

    override fun onPause() {
        super.onPause()
        binding.shimmerViewContainer.stopShimmerAnimation()
        super.onPause()
    }




}