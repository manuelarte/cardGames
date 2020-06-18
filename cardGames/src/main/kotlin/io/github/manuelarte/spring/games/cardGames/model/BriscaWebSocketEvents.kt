package io.github.manuelarte.spring.games.cardGames.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.util.*

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = BriscaRoomStatus::class, name = "roomStatus"),
        JsonSubTypes.Type(value = BriscaRoomCardPlayedEvent::class, name = "cardPlayed"),
        JsonSubTypes.Type(value = BriscaRoomPlayerTurnChangedEvent::class, name = "playerTurnChanged"),
        JsonSubTypes.Type(value = BriscaRoomRoundWinnerEvent::class, name = "roundWinner"),
        JsonSubTypes.Type(value = BriscaRoomCardGivenEvent::class, name = "cardGiven"))
interface WebSocketBriscaRoomEvent

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = BriscaPlayerStatus::class, name = "playerStatus"),
        JsonSubTypes.Type(value = BriscaPlayerCardGivenEvent::class, name = "cardGiven"))
interface WebSocketBriscaPlayerEvent

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
data class BriscaRoomStatus(val id: UUID, val numberOfPlayers: Int, val players: List<String>,
                                  val playerTurn: String?, val round: List<PlayerCardRound>, val drivenCard: Card):
        WebSocketBriscaRoomEvent {
    class PlayerCardRound(val player: String, val card: Card)
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
data class BriscaRoomCardPlayedEvent(val id: UUID, val player: String, val card: Card): WebSocketBriscaRoomEvent

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
data class BriscaRoomPlayerTurnChangedEvent(val id: UUID, val playerTurn: String): WebSocketBriscaRoomEvent

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
data class BriscaRoomRoundWinnerEvent(val id: UUID, val winner: String, val cards: List<Card>): WebSocketBriscaRoomEvent

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
data class BriscaRoomCardGivenEvent(val id: UUID, val player: String): WebSocketBriscaRoomEvent


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
data class BriscaPlayerStatus(val id: UUID, val cards: List<Card>): WebSocketBriscaPlayerEvent

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
data class BriscaPlayerCardGivenEvent(val id: UUID, val player: String, val card: Card): WebSocketBriscaPlayerEvent