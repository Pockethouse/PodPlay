package com.markbowen.podplay.model

import java.util.*

//This defines the data for a single podcast. Here’s an explanation of each property:
data class Podcast(
    var feedUrl: String = "",
    var feedTitle: String = "",
    var feedDesc: String = "",
    var imageUrl: String = "",
    var lastUpdated: Date = Date(),
    var episodes: List<Episode> = listOf()
)