package com.markbowen.podplay.model

import java.util.*

//This defines the data for a single podcast episode. These properties are required for
//display, management, or playback of an episode. Here’s an explanation for each
//property:
data class Episode (
    var guid: String = "",
    var title: String = "",
    var description: String = "",
    var mediaUrl: String = "",
    var mimeType: String = "",
    var releaseDate: Date = Date(),
    var duration: String = ""
)