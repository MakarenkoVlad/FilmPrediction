import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
private fun main(): Unit = runBlocking {
    DatabaseDao().apply {
        val models = getModels()
        models.forEach { println(it) }
        println(models.count())
        println()
    }
}