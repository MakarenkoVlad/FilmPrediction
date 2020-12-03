package robbery

import Models
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

private fun main() {
    Database.connect("jdbc:sqlite:/database/data.db", "org.sqlite.JDBC")

    transaction {
        SchemaUtils.create(Models)
    }
}