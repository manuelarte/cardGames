package io.github.manuelarte.spring.games.cardGames.commands.api

import io.github.manuelarte.spring.games.cardGames.model.Card
import java.util.*

data class PlayerJoinedEvent(val id: UUID, val player: String)

data class BriscaRoomCreatedEvent(val id: UUID, val seed: Long, val creator: String, val numberOfPlayers: Int)

data class BriscaCardPlayedEvent(val id: UUID, val player: String, val card: Card)

data class BriscaPlayerTurnChangedEvent(val id: UUID, val playerTurn: String)

data class BriscaStartedEvent(val id: UUID, val playerTurn: String, val players: List<String>, val drivenCard: Card)

data class BriscaRoundWinnerEvent(val id: UUID, val winner: String, val cards: List<Card>)

data class BriscaCardGivenEvent(val id: UUID, val player: String, val card: Card)