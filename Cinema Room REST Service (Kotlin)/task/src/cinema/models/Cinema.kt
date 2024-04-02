package cinema.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Cinema(
    @get:JsonProperty("total_rows")
    @set:JsonProperty("total_rows")
    var totalRows: Int,

    @get:JsonProperty("total_columns")
    @set:JsonProperty("total_columns")
    var totalCols: Int,

    @get:JsonProperty("available_seats")
    @set:JsonProperty("available_seats")
    var availableSeats: MutableList<Seat>,

    @JsonIgnore
    val allSeats: MutableList<Seat>,

    @JsonIgnore
    val soldSeats: MutableList<Seat>
) {
    init {
        for (i in 1..totalRows) {
            for (j in 1..totalCols) {
                val price = if (i <= 4) 10 else 8
                val seat = Seat(UUID.randomUUID(), i, j, true, price)
                availableSeats.add(seat)
                allSeats.add(seat)
            }
        }
    }
}