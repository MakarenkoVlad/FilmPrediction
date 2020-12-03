package robbery

import Models
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

private fun main() {
    Database.connect("jdbc:sqlite:/database/data.db", "org.sqlite.JDBC")

    transaction {
        val query = Models.selectAll()
        query.forEach {
            println("Row")
            it.fieldIndex.forEach { t, u -> println(it[t]) }
            println()
        }
    }
}