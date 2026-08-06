package com.cinetrack.app.di

import com.cinetrack.app.data.repository.LibraryRepository
import com.cinetrack.app.data.repository.LibraryRepositoryImpl
import com.cinetrack.app.data.repository.TmdbRepository
import com.cinetrack.app.data.repository.TmdbRepositoryImpl
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
    abstract fun bindTmdbRepository(impl: TmdbRepositoryImpl): TmdbRepository

    @Binds
    @Singleton
    abstract fun bindLibraryRepository(impl: LibraryRepositoryImpl): LibraryRepository
}
