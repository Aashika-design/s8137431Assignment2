package com.aashika.assignment2.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DashboardResponseDto(
    @SerializedName("entities") val entities: List<MovieDto>,
    @SerializedName("entityTotal") val entityTotal: Int
)

data class MovieDto(
    @SerializedName("title") val title: String,
    @SerializedName("director") val director: String,
    @SerializedName("genre") val genre: String,
    @SerializedName("releaseYear") val releaseYear: Int,
    @SerializedName("description") val description: String
)
