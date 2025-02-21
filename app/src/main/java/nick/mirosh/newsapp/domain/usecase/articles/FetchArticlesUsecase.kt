package nick.mirosh.newsapp.domain.usecase.articles

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import nick.mirosh.newsapp.data.repository.NewsRepository
import nick.mirosh.newsapp.di.IoDispatcher
import nick.mirosh.newsapp.di.Universal
import nick.mirosh.newsapp.domain.Resource
import nick.mirosh.newsapp.domain.models.Article
import javax.inject.Inject

class FetchArticlesUsecase @Inject constructor(
    @Universal private val repository: NewsRepository,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(): Resource<List<Article>> {
        return withContext(coroutineDispatcher) {
            repository.getNewsArticles()
        }
    }
}