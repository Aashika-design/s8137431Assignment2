package com.aashika.assignment2.di

import com.aashika.assignment2.data.repository.AuthRepository
import com.aashika.assignment2.data.repository.AuthRepositoryImpl
import com.aashika.assignment2.data.repository.MovieRepository
import com.aashika.assignment2.data.repository.MovieRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindMovieRepository(impl: MovieRepositoryImpl): MovieRepository
}
