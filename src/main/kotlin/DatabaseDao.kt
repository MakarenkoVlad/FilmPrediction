import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DatabaseDao {
    init {
        Database.connect("jdbc:sqlite:/database/data.db", "org.sqlite.JDBC")
    }

    private var pointer = 0

    suspend fun getModels(): List<Model> = suspendCoroutine {
        val models = mutableListOf<Model>()
        transaction {
            val query = Models.selectAll()
            query.forEach {
                val model = getModel(it)
                models.add(model!!)
            }
            it.resume(models)
        }
    }

//    suspend fun getModel(id: Int): Model? = suspendCoroutine {
//        transaction {
//            val row = Models.select {
//                Models.id eq pointer++
//            }.firstOrNull()
//            it.resume(getModel(row))
//        }
//    }
//
//    @ExperimentalCoroutinesApi
//    fun getModelsFlow(): Flow<Model> = callbackFlow {
//        transaction {
//            sendBlocking(getModel(Models.slice(seq.nextVal()).selectAll().single())!!)
//        }
//
//        awaitClose {  }
//    }

    private fun getModel(it: ResultRow?) = if (it == null) null else Model(
        rating = it[Models.rating],
        languages = it[Models.languages].split(",").count(),
        title = it[Models.title],
        genres = it[Models.genres].split(",").count(),
        runtime = it[Models.runtime],
    )
}