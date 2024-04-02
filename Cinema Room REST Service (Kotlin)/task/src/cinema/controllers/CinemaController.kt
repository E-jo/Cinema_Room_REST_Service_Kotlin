package cinema.controllers

import cinema.models.Cinema
import cinema.models.PurchaseRequest
import cinema.models.ReturnRequest
import cinema.models.Seat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class CinemaController {
    val cinema = Cinema(
        9,
        9,
        mutableListOf(),
        mutableListOf(),
        mutableListOf()
    )
    val seatComparator = compareBy<Seat>({ it.row }, { it.column })

    @GetMapping("/seats")
    fun getCinemaInfo(): ResponseEntity<*> {
        return ResponseEntity(cinema, HttpStatus.OK)
    }

    @PostMapping("/purchase")
    fun purchaseTicket(@RequestBody request: PurchaseRequest): ResponseEntity<*> {
        if (request.row < 1 || request.row > cinema.totalRows
            || request.column < 1 || request.column > cinema.totalCols
        ) {

            return ResponseEntity(
                mapOf(
                    "error" to "The number of a row or a column is out of bounds!"
                ), HttpStatus.BAD_REQUEST
            )
        }

        var targetSeat: Seat? = null
        for (seat in cinema.availableSeats) {
            if (seat.row == request.row && seat.column == request.column) {
                if (!seat.available) {
                    return ResponseEntity(
                        mapOf("error" to "The ticket has been already purchased!"),
                        HttpStatus.BAD_REQUEST
                    )
                } else {
                    seat.available = false
                    targetSeat = seat
                }
            }
        }

        if (targetSeat == null) {
            return ResponseEntity(
                mapOf("error" to "The ticket has been already purchased!"),
                HttpStatus.BAD_REQUEST
            )
        } else {
            cinema.availableSeats.remove(targetSeat)
            cinema.soldSeats.add(targetSeat)
            println("Removing seat ${targetSeat.row}, ${targetSeat.column}")
            println("Available seats: ${cinema.availableSeats.size}")
        }

        val price = if (request.row <= 4) 10 else 8
        val response = mapOf(
            "token" to targetSeat.token,
            "ticket" to mapOf(
                "row" to request.row,
                "column" to request.column,
                "price" to price
            )
        )
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping("/return")
    fun returnTicket(@RequestBody request: ReturnRequest): ResponseEntity<*> {
        println(request.toString())
        for (seat in cinema.allSeats) {
            if (seat.token.toString() == request.token) {
                if (!cinema.availableSeats.contains(seat)) {
                    cinema.availableSeats.add(seat)
                } else {
                    return ResponseEntity(mapOf("error" to "Wrong token!"), HttpStatus.BAD_REQUEST)
                }
                cinema.soldSeats.remove(seat)
                seat.available = true
                cinema.availableSeats = cinema.availableSeats
                    .sortedWith(seatComparator)
                    .toMutableList()
                println("Available seats: ${cinema.availableSeats.size}")
                val response = mapOf(
                    "returned_ticket" to mapOf(
                        "row" to seat.row,
                        "column" to seat.column,
                        "price" to seat.price
                    )
                )
                return ResponseEntity(response, HttpStatus.OK)
            }
        }
        return ResponseEntity(mapOf("error" to "Wrong token!"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/stats")
    fun getStats(
        @RequestParam(
            required = false,
            defaultValue = "password",
            value = "password"
        ) pass: String
    ): ResponseEntity<*> {
        var response: ResponseEntity<*>? = null
        var currentIncome = 0

        if (pass.equals("super_secret", ignoreCase = true)) {
            for (seat in cinema.soldSeats) {
                currentIncome += seat.price
            }
            val r: MutableMap<String, Int> = HashMap()
            r["current_income"] = currentIncome
            r["number_of_purchased_tickets"] = cinema.soldSeats.size
            r["number_of_available_seats"] = cinema.availableSeats.size

            response = ResponseEntity<Map<String, Int>>(r, HttpStatus.OK)
        } else {
            response = ResponseEntity(mapOf("error" to "The password is wrong!"), HttpStatus.UNAUTHORIZED)
        }

        return response
    }

}


