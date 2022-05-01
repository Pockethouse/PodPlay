package com.markbowen.podplay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.markbowen.podplay.repository.ItunesRepo
import com.markbowen.podplay.service.PodcastResponse
import com.markbowen.podplay.util.DateUtils


class SearchViewModel(application: Application) :
    AndroidViewModel(application) {

    // add a property for an ItunesRepo, which will fetch the information
    var iTunesRepo: ItunesRepo? = null

    // define a data class within the view model that has only the data that’s
//necessary for the View
    data class PodcastSummaryViewData(
        var name: String? = "",
        var lastUpdated: String? = "",
        var imageUrl: String? = "",
        var feedUrl: String? = ""
    )


    //Next, add a helper method to convert from the raw model data to the view data
    private fun itunesPodcastToPodcastSummaryView(itunesPodcast: PodcastResponse.ItunesPodcast):
            PodcastSummaryViewData {
        return PodcastSummaryViewData(
            itunesPodcast.collectionCensoredName,
            DateUtils.jsonDateToShortDate(itunesPodcast.releaseDate),
            itunesPodcast.artworkUrl30,
            itunesPodcast.feedUrl)
    }
    //define a method to perform the search, which eventually gets called by
    //PodcastActivity:

    // The first parameter is the search term
    suspend fun searchPodcasts(term: String):
            List<PodcastSummaryViewData> {
        // iTunesRepo is used to perform the search asynchronously
        val results = iTunesRepo?.searchByTerm(term)
        // Check if the results are not null and the call is successful
        if (results != null && results.isSuccessful) {
            // Get the podcasts from the body
            val podcasts = results.body()?.results
            // Check if the podcasts list is not empty
            if (!podcasts.isNullOrEmpty()) {
                // Map them to PodcastSummaryViewData objects
                return podcasts.map { podcast ->
                    itunesPodcastToPodcastSummaryView(podcast)
                }
            }
        }
        // If the results are null, then you return an empty list
        return emptyList()
    }
}