package com.aashika.assignment2.data.model

import com.aashika.assignment2.data.remote.dto.MovieDto

data class Movie(
    val title: String,
    val director: String,
    val genre: String,
    val releaseYear: Int,
    val description: String
)

fun MovieDto.toDomain(): Movie = Movie(
    title = title,
    director = director,
    genre = genre,
    releaseYear = releaseYear,
    description = description
)
