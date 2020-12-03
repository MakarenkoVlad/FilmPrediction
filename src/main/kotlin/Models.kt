import org.jetbrains.exposed.dao.id.IntIdTable

object Models : IntIdTable() {
    val rating = double("rating")
    val title = varchar("title", 8000)
    val genres = varchar("genres", 8000)
    val languages = varchar("languages", 8000)
    val runtime = integer("runtime")
}