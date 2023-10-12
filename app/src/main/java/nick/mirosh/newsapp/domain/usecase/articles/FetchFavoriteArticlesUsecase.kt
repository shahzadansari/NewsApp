package nick.mirosh.newsapp.domain.usecase.articles

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import nick.mirosh.newsapp.data.repository.NewsRepository
import nick.mirosh.newsapp.di.Cache
import nick.mirosh.newsapp.di.IoDispatcher
import nick.mirosh.newsapp.domain.DomainState
import nick.mirosh.newsapp.entity.Article
import javax.inject.Inject

class FetchFavoriteArticlesUsecase @Inject constructor(
    @Cache private val repository: NewsRepository,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(): DomainState<List<Article>> {
        return withContext(coroutineDispatcher) {
            repository.getFavoriteArticles()
        }
    }
}