package robbery.model

data class MovieResponse(
    val Total_results: String,
    val movie_results: List<MovieResult>,
    val results: Int,
    val status: String,
    val status_message: String
)