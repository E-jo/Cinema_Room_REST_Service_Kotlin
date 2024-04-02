package cinema.models

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*

data class Seat(
    @JsonIgnore
    val token: UUID,
    val row: Int,
    val column: Int,
    @JsonIgnore
    var available: Boolean,
    val price: Int
)