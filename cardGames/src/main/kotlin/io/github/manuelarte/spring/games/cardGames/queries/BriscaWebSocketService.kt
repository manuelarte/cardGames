package io.github.manuelarte.spring.games.cardGames.queries

import io.github.manuelarte.spring.games.cardGames.commands.api.BriscaCardGivenEvent
import io.github.manuelarte.spring.games.cardGames.commands.api.BriscaCardPlayedEvent
import io.github.manuelarte.spring.games.cardGames.commands.api.BriscaPlayerTurnChangedEvent
import io.github.manuelarte.spring.games.cardGames.model.BriscaPlayerStatus
import io.github.manuelarte.spring.games.cardGames.model.BriscaRoomRoundWinnerEvent
import io.github.manuelarte.spring.games.cardGames.model.BriscaRoomStatus
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class BriscaWebSocketService(private val simpMessagingTemplate: SimpMessagingTemplate) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun sendRoomBriscaStatus(briscaRoom: BriscaRoom) {
        log.debug("WS Sending Room brisca status: {}", briscaRoom)
        simpMessagingTemplate.convertAndSend( "/topic/games/brisca/${briscaRoom.id}",
                BriscaRoomStatus(briscaRoom.id, briscaRoom.numberOfPlayers, briscaRoom
                        .players, briscaRoom.playerTurn, briscaRoom.round.map { BriscaRoomStatus.PlayerCardRound(it
                        .player, it.card) }.toList(), briscaRoom.drivenCard!!))
    }

    fun sendRoomBriscaStarted() {
        // to do
    }

    fun sendPlayerBriscaStatuses(playerHands: Map<String, PlayerBriscaRoom>) {
        playerHands.forEach {
            sendPlayerBriscaStatus(it.value)
        }
    }

    fun sendRoomBriscaCardPlayedEvent(evt: BriscaCardPlayedEvent) {
        log.debug("WS Sending BriscaRoomCardPlayedEvent: {}", evt)
        simpMessagingTemplate.convertAndSend( "/topic/games/brisca/${evt.id}",
                io.github.manuelarte.spring.games.cardGames.model.BriscaRoomCardPlayedEvent(evt.id,
                        evt.player, evt.card))
    }

    fun sendRoomBriscaPlayerTurnChangedEvent(evt: BriscaPlayerTurnChangedEvent) {
        log.debug("WS Sending BriscaPlayerTurnChangedEvent: {}", evt)
        simpMessagingTemplate.convertAndSend( "/topic/games/brisca/${evt.id}",
                io.github.manuelarte.spring.games.cardGames.model.BriscaRoomPlayerTurnChangedEvent(evt.id,
                        evt.playerTurn))
    }

    fun sendBriscaRoundWinnerEvent(evt: BriscaRoomRoundWinnerEvent) {
        log.debug("WS Sending BriscaRoomRoundWinnerEvent: {}", evt)
        simpMessagingTemplate.convertAndSend( "/topic/games/brisca/${evt.id}",
                BriscaRoomRoundWinnerEvent(evt.id, evt.winner, evt.cards))
    }

    fun sendPlayerBriscaStatus(playerBriscaRoom: PlayerBriscaRoom) {
        log.debug("WS Sending RoomBriscaPlayerStatus: {}", playerBriscaRoom)
        simpMessagingTemplate.convertAndSend( "/topic/games/brisca/${playerBriscaRoom
                .id}/player/${playerBriscaRoom.player}",
                BriscaPlayerStatus(playerBriscaRoom.id, playerBriscaRoom.playerHand))
    }

    fun sendPlayerBriscaCardGivenEvent(evt: BriscaCardGivenEvent) {
        log.debug("WS Sending BriscaPlayerCardGivenEvent: {}", evt)
        simpMessagingTemplate.convertAndSend( "/topic/games/brisca/${evt.id}/player/${evt.player}",
                io.github.manuelarte.spring.games.cardGames.model.BriscaPlayerCardGivenEvent(evt.id, evt.player, evt.card))
    }


}