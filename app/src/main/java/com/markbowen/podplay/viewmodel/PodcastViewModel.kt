package com.markbowen.podplay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.markbowen.podplay.model.Episode
import com.markbowen.podplay.model.Podcast
import com.markbowen.podplay.repository.PodcastRepo
import kotlinx.coroutines.launch
import java.util.*

//This defines the PodcastViewModel for fragment_podcast

class PodcastViewModel(application: Application) :
    AndroidViewModel(application) {
    var podcastRepo: PodcastRepo? = null
    var activePodcastViewData: PodcastViewData? = null
    private val _podcastLiveData = MutableLiveData<PodcastViewData?
            >()
    val podcastLiveData: LiveData<PodcastViewData?> =
        _podcastLiveData

    //PodcastViewData contains everything you
    //need to display the details of a podcast.
    //This defines the PodcastViewModel for the detail Fragment.
    data class PodcastViewData(
        var subscribed: Boolean = false,
        var feedTitle: String? = "",
        var feedUrl: String? = "",
        var feedDesc: String? = "",
        var imageUrl: String? = "",
        var episodes: List<EpisodeViewData>
    )
    data class EpisodeViewData (
        var guid: String? = "",
        var title: String? = "",
        var description: String? = "",
        var mediaUrl: String? = "",
        var releaseDate: Date? = null,
        var duration: String? = ""
    )


    //This method converts a Podcast model to a PodcastViewData object

    private fun podcastToPodcastView(podcast: Podcast):
            PodcastViewData {
        return PodcastViewData(
            false,
            podcast.feedTitle,
            podcast.feedUrl,
            podcast.feedDesc,
            podcast.imageUrl,
            episodesToEpisodesView(podcast.episodes)
        )
    }

    //This method uses map to do the following:
    //• Iterate over a list of Episode models.
    //• Convert Episode models to EpisodeViewData objects.
    //• Collect everything into a list.

    //The repo returns a list of Episode models, so you need a method to convert these
    //models into EpisodeViewData view models
    private fun episodesToEpisodesView(episodes: List<Episode>):
            List<EpisodeViewData> {
        return episodes.map {
            EpisodeViewData(
                it.guid,
                it.title,
                it.description,
                it.mediaUrl,
                it.releaseDate,
                it.duration
            )
        }
    }

    // getPodcast() takes a PodcastSummaryViewData object and returns a
    //PodcastViewData or null.
    fun getPodcast(podcastSummaryViewData: SearchViewModel.PodcastSummaryViewData) {
        podcastSummaryViewData.feedUrl?.let { url ->
            viewModelScope.launch {
                podcastRepo?.getPodcast(url)?.let {
                    it.feedTitle = podcastSummaryViewData.name ?: ""
                    it.imageUrl = podcastSummaryViewData.imageUrl ?: ""
                    _podcastLiveData.value = podcastToPodcastView(it)
                } ?: run {
                    _podcastLiveData.value = null
                }
            }
        } ?: run {
            _podcastLiveData.value = null
        }
    }
}