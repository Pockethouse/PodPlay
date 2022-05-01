package com.markbowen.podplay.service

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesService {
    // @GET annotation takes a single
    //parameter: The path of the endpoint that should be called
    @GET("/search?media=podcast")
    // This annotation tells Retrofit
    //that this parameter should be added as a query term
    suspend fun searchPodcastByTerm(@Query("term") term: String):
            Response<PodcastResponse>
    // You define a companion object in the ItunesService interface
    companion object {
        //
        val instance: ItunesService by lazy {
            // This is the first part of the lazy lambda method. Retrofit.Builder() is used to
            //create a retrofit builder object
            val retrofit = Retrofit.Builder()
                .baseUrl("https://itunes.apple.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
// Finally, you call create<ItunesService>() on the retrofit builder object to
//create the ItunesService instance
            retrofit.create(ItunesService::class.java)
        }
    }
}