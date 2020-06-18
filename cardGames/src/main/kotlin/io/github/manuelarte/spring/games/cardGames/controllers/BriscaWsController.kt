package io.github.manuelarte.spring.games.cardGames.controllers

import io.github.manuelarte.spring.games.cardGames.commands.api.BriscaPlayCardCommand
import io.github.manuelarte.spring.games.cardGames.model.Card
import io.github.manuelarte.spring.games.cardGames.queries.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import java.util.*


@Controller
class BriscaWsController(
        private val queryGateway: QueryGateway,
        private val briscaWebSocketService: BriscaWebSocketService,
        private val commandGateway: CommandGateway) {

    @MessageMapping("/game/{id}")
    fun gameStatus(@DestinationVariable id: UUID) {
        queryGateway.query(GetBriscaRoomStatus(id), BriscaRoom::class.java).thenApply {
            briscaWebSocketService.sendRoomBriscaStatus(it)
        }
    }

    @MessageMapping("/game/{id}/player/{playerId}")
    fun playerStatus(@DestinationVariable id: UUID, @DestinationVariable playerId: String) {
        queryGateway.query(GetRoomBriscaPlayerStatus(id, playerId), PlayerBriscaRoom::class.java).thenApply {
            briscaWebSocketService.sendPlayerBriscaStatus(it)
        }
    }

    @MessageMapping("/game/{id}/player/{playerId}/move")
    fun move(@DestinationVariable id: UUID, @DestinationVariable playerId: String, card: Card) {
        commandGateway.send<UUID>(BriscaPlayCardCommand(id, playerId, card))
    }

}