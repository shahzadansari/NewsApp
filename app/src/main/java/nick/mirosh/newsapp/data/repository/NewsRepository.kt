package nick.mirosh.newsapp.data.repository

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import nick.mirosh.newsapp.data.DataState
import nick.mirosh.newsapp.database.ArticleDao
import nick.mirosh.newsapp.di.IoDispatcher
import nick.mirosh.newsapp.entity.Article
import nick.mirosh.newsapp.entity.asDatabaseArticle
import nick.mirosh.newsapp.entity.asDatabaseModel
import nick.mirosh.newsapp.entity.asDomainModel
import java.io.PrintWriter
import java.io.StringWriter
import javax.inject.Inject


const val tag = "NewsRepository"

interface NewsRepository {

    suspend fun refreshNews(): Flow<DataState<List<Article>>>

    suspend fun getFavoriteArticles(): Flow<DataState<List<Article>>>

    suspend fun updateArticle(article: Article)
}

class NewsRepositoryImpl @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val newsDataSource: NewsRemoteDataSource? = null,
    private val dao: ArticleDao
) : NewsRepository {
    override suspend fun refreshNews(): Flow<DataState<List<Article>>> {
        return withContext(coroutineDispatcher) {
            flow {
                try {
                    val networkArticles = newsDataSource?.getHeadlines() ?: emptyList()
                    if (networkArticles.isNotEmpty()) {
                        val result = dao.insertAll(networkArticles.map {
                            it.asDatabaseArticle()
                        })
                        Log.d(tag, "refreshNews: result = $result")
                    }
                } catch (e: Exception) {
                    e.logStackTrace(tag)
                    emit(DataState.Error("Error fetching headlines"))
                } finally {
                    val data = getAllArticlesFromDb()
                    emit(DataState.Success(data))
                }
            }
        }
    }

    override suspend fun getFavoriteArticles() =
        withContext(coroutineDispatcher) {
            flow {
                try {
                    emit(
                        DataState.Success(
                            dao.getLikedArticles().map {
                                it.asDomainModel()
                            })
                    )
                } catch (e: Exception) {
                    e.logStackTrace(tag)
                    emit(DataState.Error("Error fetching favorite articles"))
                }
            }
        }

    override suspend fun updateArticle(article: Article) {
        withContext(coroutineDispatcher) {
            dao.insert(article.asDatabaseModel())
            getAllArticlesFromDb()
        }
    }

    private suspend fun getAllArticlesFromDb() =
        dao.getAllArticles()
            .map { it.asDomainModel() }
            .sortedBy { it.url }
}


fun Throwable.logStackTrace(tag: String) {
    val sw = StringWriter()
    this.printStackTrace(PrintWriter(sw))
    val exceptionAsString = sw.toString()
    Log.e(tag, exceptionAsString)
}
