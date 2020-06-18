package io.github.manuelarte.spring.games.cardGames.exceptions

import java.lang.Exception
import java.util.*

data class RoomNotFoundException(val id: UUID): Exception()