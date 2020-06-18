package io.github.manuelarte.spring.games.cardGames.controllers.dto

import java.util.*

data class BriscaRoomCreatedDto(val id: UUID, val endpoint: String, val subscribeTo: String,
                                val numberOfPlayers: Int, val players: List<String>)

// events to be used with the WebSocket
data class RoomBriscaStatusDto(val id: UUID, val numberOfPlayers: Int, val players: List<String>,
                               val endpoint: String, val subscribeTo: String)
data class UserJoinedBriscaRoomEvent(val id: UUID, val user: String)