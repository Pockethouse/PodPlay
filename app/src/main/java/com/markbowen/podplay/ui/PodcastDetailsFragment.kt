package com.markbowen.podplay.ui

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.markbowen.podplay.R
import com.markbowen.podplay.adapter.EpisodeListAdapter
import com.markbowen.podplay.databinding.FragmentPodcastDetailsBinding
import com.markbowen.podplay.viewmodel.PodcastViewModel



//This is the standard procedure for setting up a Fragment

class PodcastDetailsFragment : Fragment() {
    private lateinit var databinding: FragmentPodcastDetailsBinding
    private lateinit var episodeListAdapter: EpisodeListAdapter

    //activitytViewModels() is an extension function that allows the fragment to access
//and share view models from the fragment's parent activity.

    private val podcastViewModel: PodcastViewModel by activityViewModels()


    //This is a convenience method that returns an instance of PodcastDetailsFragment
    companion object {
        fun newInstance(): PodcastDetailsFragment {
            return PodcastDetailsFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The call to setHasOptionsMenu() tells Android that this Fragment wants to add
        //items to the options menu
        setHasOptionsMenu(true)
    }
    override fun onCreateView(inflater: LayoutInflater, container:
    ViewGroup?, savedInstanceState: Bundle?): View {
        databinding =
            FragmentPodcastDetailsBinding.inflate(inflater, container, false)
        return databinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState:
    Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        podcastViewModel.podcastLiveData.observe(viewLifecycleOwner,
            { viewData ->
                if (viewData != null) {
                    databinding.feedTitleTextView.text = viewData.feedTitle
                    databinding.feedDescTextView.text = viewData.feedDesc
                    activity?.let { activity ->
                        Glide.with(activity).load(viewData.imageUrl).into(databinding.feedImageView)
                    }
                    // 1
                    databinding.feedDescTextView.movementMethod = ScrollingMovementMethod()
                    // 2
                    databinding.episodeRecyclerView.setHasFixedSize(true)
                    val layoutManager = LinearLayoutManager(activity)
                    databinding.episodeRecyclerView.layoutManager = layoutManager
                    val dividerItemDecoration = DividerItemDecoration(
                        databinding.episodeRecyclerView.context, layoutManager.orientation)

                    databinding.episodeRecyclerView.addItemDecoration(dividerItemDecoration)
                    // 3
                    episodeListAdapter = EpisodeListAdapter(viewData.episodes)
                    databinding.episodeRecyclerView.adapter = episodeListAdapter
                }
            })
    }
    // onCreateOptionsMenu() inflates the menu_details options menu so its items
    //are added to the podcast Activity menu.
    override fun onCreateOptionsMenu(menu: Menu, inflater:
    MenuInflater
    ) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_details, menu)
    }

    //This first line checks to make sure there’s view data available
    //It then uses the view data to populate the title
    //and description TextView elements, as well as load the podcast image using Glide



}