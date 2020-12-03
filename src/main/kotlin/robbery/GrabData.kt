package robbery

import Models
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import robbery.model.ImdbResponse
import robbery.model.MovieResponse

private val client by lazy {
    HttpClient {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }
}

private fun main(): Unit = runBlocking(Dispatchers.IO) {

    Database.connect("jdbc:sqlite:/database/data.db", "org.sqlite.JDBC")
    val asyncResult = suspendedTransactionAsync {
        for (id in 30..40) {
            val response = getMovieResponse(client, id)

            response.movie_results.map {
                println("${it.imdb_id}_${it.title}")
                try {
                    getImdbResponse(client, it.imdb_id)
                } catch (e: Exception) {
                    null
                }
            }.forEach {
                it?.let {
                    if (it.imdbRating == "N/A" || it.Runtime == "N/A") return@let
                    println(it)
                    Models.insert { statement ->
                        statement[genres] = it.Genre
                        statement[rating] = it.imdbRating.toDouble()
                        statement[title] = it.Title
                        statement[languages] = it.Language
                        statement[runtime] = it.Runtime.split(" ").first().toInt()
                    }
                }
            }

        }

        client.close()
    }

    asyncResult.await()

//        client.use { client ->
//            val response: String =
//                client.get("https://movie-database-imdb-alternative.p.rapidapi.com/?i=tt4154796&r=json") {
//                    header("x-rapidapi-key", "7d3c783eb6mshc7fa53beeb89e44p1e7b65jsnf9c8f570e7b4")
//                    header("x-rapidapi-host", "movie-database-imdb-alternative.p.rapidapi.com")
//                }
//            println(response)
//        }
}

private suspend fun getImdbResponse(client: HttpClient, id: String): ImdbResponse =
    client.get("https://movie-database-imdb-alternative.p.rapidapi.com/?i=$id&r=json") {
        header("x-rapidapi-key", "7d3c783eb6mshc7fa53beeb89e44p1e7b65jsnf9c8f570e7b4")
        header("x-rapidapi-host", "movie-database-imdb-alternative.p.rapidapi.com")
    }

private suspend fun getMovieResponse(client: HttpClient, page: Int): MovieResponse =
    client.get("https://movies-tvshows-data-imdb.p.rapidapi.com/?type=get-movies-byyear&year=2020&page=$page") {
        header("x-rapidapi-key", "7d3c783eb6mshc7fa53beeb89e44p1e7b65jsnf9c8f570e7b4")
        header("x-rapidapi-host", "movies-tvshows-data-imdb.p.rapidapi.com")
    }