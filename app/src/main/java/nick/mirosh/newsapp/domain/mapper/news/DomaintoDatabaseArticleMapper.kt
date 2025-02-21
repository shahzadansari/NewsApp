package nick.mirosh.newsapp.domain.mapper.news

import nick.mirosh.newsapp.domain.mapper.BaseMapper
import nick.mirosh.newsapp.domain.models.Article
import nick.mirosh.newsapp.data.models.DatabaseArticle
import nick.mirosh.newsapp.domain.models.asDatabaseModel
import javax.inject.Inject

class DomaintoDatabaseArticleMapper @Inject constructor() : BaseMapper<List<Article>, List<DatabaseArticle>>() {
    override fun map(data: List<Article>) =
        data.map {
            it.asDatabaseModel()
        }
}