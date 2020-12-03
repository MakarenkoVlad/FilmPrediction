package analysis

import DatabaseDao
import Model
import kotlinx.coroutines.*
import java.io.*

private fun main() = runBlocking(Dispatchers.IO) {
    Calculator().calculateConstants()
}

class Calculator {

    var counter = 0

    private val dao = DatabaseDao()

    companion object {
        const val PATH = "C:\\ideaProjects\\FilmPrediction\\src\\main\\kotlin\\analysis\\constants.txt"
        const val DIFF = 0.0001
    }

    private lateinit var constants: Constants

    suspend fun calculatePrediction(genres: Double, runtime: Double, langs: Double) = withContext(Dispatchers.Default) {
        getConstants()
        println("constants: $constants")
        println("genres: $genres, runtime: $runtime, langs: $langs")
        val models = dao.getModels()
        val maxRatingAsync = async { models.maxOf { it.rating } }
        val maxGenresAsync = async { models.maxOf { it.genres } }
        val maxLanguagesAsync = async { models.maxOf { it.languages } }
        val maxRuntimeAsync = async { models.maxOf { it.runtime } }

        val maxRating = maxRatingAsync.await()
        val maxGenres = maxGenresAsync.await()
        val maxLanguages = maxLanguagesAsync.await()
        val maxRuntime = maxRuntimeAsync.await()

        println("maxGenres: $maxGenres, maxLangs: $maxLanguages, maxRuntime: $maxRuntime")

        val genresK = constants.genres * (genres / maxGenres)
        val languagesK = constants.languages * (langs / maxLanguages)
        val runtimeK = constants.runtime * (runtime / maxRuntime)

        println("genresK: $genresK, languagesK: $languagesK, runtimeK: $runtimeK")
        val result = (genresK + languagesK + runtimeK) * maxRating
        println("result: $result")
        result
    }

    suspend fun calculateConstants() {
        getConstants()

        println("Constants at start: $constants")
        val models = dao.getModels()
        repeat(100) { updateConstants(models) }
        println("Constants at end: $constants, counter: $counter")
        saveConstants()
    }

    private suspend fun updateConstants(models: List<Model>) {
        withContext(Dispatchers.Default) {
            val maxRatingAsync = async { models.maxOf { it.rating } }
            val maxGenresAsync = async { models.maxOf { it.genres } }
            val maxLanguagesAsync = async { models.maxOf { it.languages } }
            val maxRuntimeAsync = async { models.maxOf { it.runtime } }

            val maxRating = maxRatingAsync.await()
            val maxGenres = maxGenresAsync.await()
            val maxLanguages = maxLanguagesAsync.await()
            val maxRuntime = maxRuntimeAsync.await()

            fun internalUpdateConstants(model: Model) {
                counter++
                val genresK = constants.genres * (model.genres.toDouble() / maxGenres)
                val languagesK = constants.languages * (model.languages.toDouble() / maxLanguages)
                val runtimeK = constants.runtime * (model.runtime.toDouble() / maxRuntime)
                val predictedRating =
                    maxRating * (genresK + languagesK + runtimeK)
                val max = maxOf(genresK, languagesK, runtimeK)
                val min = minOf(genresK, languagesK, runtimeK)
                if (predictedRating > model.rating) {
                    when (max) {
                        genresK -> constants.genres -= DIFF
                        languagesK -> constants.languages -= DIFF
                        runtimeK -> constants.runtime -= DIFF
                    }
                    when (min) {
                        genresK -> constants.genres += DIFF
                        languagesK -> constants.languages += DIFF
                        runtimeK -> constants.runtime += DIFF
                    }
                } else {
                    when (min) {
                        genresK -> constants.genres -= DIFF
                        languagesK -> constants.languages -= DIFF
                        runtimeK -> constants.runtime -= DIFF
                    }
                    when (max) {
                        genresK -> constants.genres += DIFF
                        languagesK -> constants.languages += DIFF
                        runtimeK -> constants.runtime += DIFF
                    }
                }
            }

            models.forEach(::internalUpdateConstants)
        }
    }

    private fun getConstants() {
        val file = File(PATH)
        constants = if (file.exists()) {
            try {
                val fileInputStream = FileInputStream(file)
                val inputStream = ObjectInputStream(fileInputStream)

                inputStream.readObject() as Constants
            } catch (e: Exception) {
                println("Something bad happened")
                throw e
            }
        } else {
            file.createNewFile()
            Constants(1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0)
        }
    }

    private fun saveConstants() {
        try {
            val file = FileOutputStream(PATH)
            val outStream = ObjectOutputStream(file)
            outStream.writeObject(constants)
        } catch (e: Exception) {
            println(e)
            throw e
        }
    }

}