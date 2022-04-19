package com.ferelin.core.domain.usecase

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.News
import com.ferelin.core.domain.repository.NewsRepository
import kotlinx.coroutines.flow.*

interface NewsUseCase {
    val newsLce: Flow<LceState>
    val newsFetchLce: Flow<LceState>
    fun getNewsBy(companyId: CompanyId): Flow<List<News>>
    suspend fun fetchNews(companyId: CompanyId, companyTicker: String)
}

internal class NewsUseCaseImpl(
    private val newsRepository: NewsRepository,
    private val dispatchersProvider: DispatchersProvider
) : NewsUseCase {
    override fun getNewsBy(companyId: CompanyId): Flow<List<News>> {
        return newsRepository.getAllBy(companyId)
            .onStart { newsLceState.value = LceState.Loading }
            .onEach { newsLceState.value = LceState.Content }
            .catch { e -> newsLceState.value = LceState.Error(e.message) }
            .flowOn(dispatchersProvider.IO)
    }

    private val newsLceState = MutableStateFlow<LceState>(LceState.None)
    override val newsLce: Flow<LceState> = newsLceState.asStateFlow()

    override suspend fun fetchNews(companyId: CompanyId, companyTicker: String) {
        newsFetchLceState.value = LceState.Loading
        newsRepository.fetchNews(companyId, companyTicker)
            .onSuccess { newsFetchLceState.value = LceState.Content }
            .onFailure { newsFetchLceState.value = LceState.Error(it.message) }
    }

    private val newsFetchLceState = MutableStateFlow<LceState>(LceState.None)
    override val newsFetchLce: Flow<LceState> = newsFetchLceState.asStateFlow()
}