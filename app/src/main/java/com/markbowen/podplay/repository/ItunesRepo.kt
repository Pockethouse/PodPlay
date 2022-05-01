package com.markbowen.podplay.repository

import com.markbowen.podplay.service.ItunesService

//The next step is to hide the service behind a repository as you did with the database
//in PlaceBook.

//You define the primary constructor for ItunesRepo to require an existing
//instance of the ItunesService interface.
class ItunesRepo(private val itunesService: ItunesService) {
    // ItunesRepo contains a single method named searchByTerm
    suspend fun searchByTerm(term: String) =
        itunesService.searchPodcastByTerm(term)
    //You call searchPodcastByTerm() and pass in the search term. This returns a
    //Retrofit Response object of PodcastResponse.
}