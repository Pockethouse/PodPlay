package com.markbowen.podplay.ui

import androidx.appcompat.widget.SearchView
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.markbowen.podplay.R
import com.markbowen.podplay.adapter.PodcastListAdapter
import com.markbowen.podplay.databinding.ActivityPodcastBinding
import com.markbowen.podplay.repository.ItunesRepo
import com.markbowen.podplay.repository.PodcastRepo
import com.markbowen.podplay.service.FeedService
import com.markbowen.podplay.service.ItunesService
import com.markbowen.podplay.service.RssFeedService
import com.markbowen.podplay.viewmodel.PodcastViewModel
import com.markbowen.podplay.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PodcastActivity : AppCompatActivity(),
    PodcastListAdapter.PodcastListAdapterListener {

    private lateinit var databinding: ActivityPodcastBinding
    private val searchViewModel by viewModels<SearchViewModel>()
    private lateinit var podcastListAdapter: PodcastListAdapter
    private lateinit var searchMenuItem: MenuItem
    private val podcastViewModel by viewModels<PodcastViewModel>()

    val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = ActivityPodcastBinding.inflate(layoutInflater)
        setContentView(databinding.root)
        setupToolbar()
        setupViewModels()
        updateControls()
        handleIntent(intent)
        addBackStackListener()

}


    //creates an instance of the ItunesService and then uses ViewModelProviders
//to get an instance of the SearchViewModel


    private fun createSubscription() {
        podcastViewModel.podcastLiveData.observe(this, {
            hideProgressBar()
            if (it != null) {
                showDetailsFragment()
            } else {
                showError("Error loading feed")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // First, you inflate the options menu
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)
        // The search action menu item is found within the options menu, and the search
        //view is taken from the item’s actionView property
        searchMenuItem = menu.findItem(R.id.search_item)

        val searchView = searchMenuItem.actionView as SearchView
        // The system SearchManager object is loaded. SearchManager provides some key
        //functionality when working with search services.
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        // You use searchManager to load the search configuration and assign it to the
        //searchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        if (supportFragmentManager.backStackEntryCount > 0) {
            databinding.podcastRecyclerView.visibility = View.INVISIBLE
        }
        return true
    }

    private fun performSearch(term: String) {
        showProgressBar()
        GlobalScope.launch {
            val results = searchViewModel.searchPodcasts(term)
            withContext(Dispatchers.Main) {
                hideProgressBar()
                databinding.toolbar.title = term
                podcastListAdapter.setSearchData(results)
            }
        }
    }


    //This method takes in an Intent and checks to see if it’s an ACTION_SEARCH. If so, it
    //extracts the search query string and passes it to performSearch().
    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY) ?: return
            performSearch(query)
        }
    }
//This method is called when the Intent is sent from the search widget. It calls
//setIntent() to make sure the new Intent is saved with the Activity
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }
//a built-in method that makes
//the toolbar act as the ActionBar for this Activity
    private fun setupToolbar() {
        setSupportActionBar(databinding.toolbar)
    }
//set up the RecyclerView with a
//PodcastListAdapter:
    private fun updateControls() {
        databinding.podcastRecyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        databinding.podcastRecyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(
            databinding.podcastRecyclerView.context, layoutManager.orientation)

        databinding.podcastRecyclerView.addItemDecoration(dividerItemDecoration)
        podcastListAdapter = PodcastListAdapter(null, this, this)
        databinding.podcastRecyclerView.adapter = podcastListAdapter
    }

    // the following method to satisfy the PodcastListAdapterListener
    //interface
    override fun onShowDetails(podcastSummaryViewData:
                               SearchViewModel.PodcastSummaryViewData) {
        podcastSummaryViewData.feedUrl?.let {
            showProgressBar()
            podcastViewModel.getPodcast(podcastSummaryViewData)
        }
    }

    //following helper methods to encapsulate showing and hiding the
    //progress bar during searching
    private fun showProgressBar() {
        databinding.progressBar.visibility = View.VISIBLE
    }
    private fun hideProgressBar() {
        databinding.progressBar.visibility = View.INVISIBLE
    }

    //This defines a tag to uniquely identify the details Fragment in the Fragment
    //Manager

    companion object {
        private const val TAG_DETAILS_FRAGMENT = "DetailsFragment"
    }
//This method either creates the details Fragment or uses an existing instance if one
//exists. Here’s a closer look at how this works:
    private fun createPodcastDetailsFragment():
            PodcastDetailsFragment {
        // You use supportFragmentManager.findFragmentByTag() to check if the
    //Fragment already exists.
        var podcastDetailsFragment = supportFragmentManager
            .findFragmentByTag(TAG_DETAILS_FRAGMENT) as
                PodcastDetailsFragment?
        // If there’s no existing fragment, you create a new one using newInstance() on
    //the Fragment’s companion object.
        if (podcastDetailsFragment == null) {
            podcastDetailsFragment =
                PodcastDetailsFragment.newInstance()
        }
    if (databinding.podcastRecyclerView.visibility ==
        View.INVISIBLE) {
        searchMenuItem.isVisible = false
    }
        return podcastDetailsFragment
    }

    private fun setupViewModels() {
        val service = ItunesService.instance
        searchViewModel.iTunesRepo = ItunesRepo(service)
        podcastViewModel.podcastRepo = PodcastRepo(RssFeedService.instance)
    }

    //
    private fun showDetailsFragment() {
        // The details fragment is created or retrieved from the fragment manage
        val podcastDetailsFragment = createPodcastDetailsFragment()
        // The fragment is added to the supportFragmentManager
        supportFragmentManager.beginTransaction().add(
            R.id.podcastDetailsContainer,
            podcastDetailsFragment, TAG_DETAILS_FRAGMENT)
            .addToBackStack("DetailsFragment").commit()
        // The main podcast RecyclerView is hidden so the only thing showing is the detail
        //Fragment
        databinding.podcastRecyclerView.visibility = View.INVISIBLE
        // The searchMenuItem is hidden so that the search icon is not shown on the
        //details screen
        searchMenuItem.isVisible = false
    }
//This displays a generic alert dialog with an error message. You’ll show this dialog to
//handle all error cases
    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok_button), null)
            .create()
            .show()
    }
//add a listener to supportFragmentManager so you’re notified
//when the back stack changes.
    private fun addBackStackListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                databinding.podcastRecyclerView.visibility = View.VISIBLE
            }
        }
    }
}