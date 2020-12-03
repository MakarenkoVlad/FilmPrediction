package share

import analysis.Calculator
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking

const val LANGS = "languages"
const val GENRES = "genres"
const val RUNTIME = "runtime"
const val PARAMS = "params"

fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                val langs = call.parameters[LANGS]
                val genres = call.parameters[GENRES]
                val runtime = call.parameters[RUNTIME]
                println("lang: $langs, genres: $genres, runtime: $runtime")
                if (langs == null || genres == null || runtime == null) {
                    call.respond("Invalid data: must not be null")
                } else {
                    try {
                        val result = Calculator().calculatePrediction(langs.toDouble(), runtime.toDouble(), langs.toDouble())
                        call.respond(result.toString())
                    } catch (e: Exception) {
                        call.respond(e)
                    }
                }
            }
        }
    }.start(false)
}