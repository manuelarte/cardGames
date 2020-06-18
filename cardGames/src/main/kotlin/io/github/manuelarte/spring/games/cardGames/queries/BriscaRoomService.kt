package io.github.manuelarte.spring.games.cardGames.queries

import io.github.manuelarte.spring.games.cardGames.commands.api.*
import io.github.manuelarte.spring.games.cardGames.controllers.dto.UserJoinedBriscaRoomEvent
import io.github.manuelarte.spring.games.cardGames.exceptions.RoomNotFoundException
import io.github.manuelarte.spring.games.cardGames.model.BriscaRoomRoundWinnerEvent
import io.github.manuelarte.spring.games.cardGames.model.PlayerCardRound
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service
import java.util.*

@Service
class BriscaRoomService(private val briscaWebSocketService: BriscaWebSocketService) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val cache: MutableMap<UUID, BriscaRoom> = mutableMapOf()

    @EventHandler
    @Suppress("unused")
    fun on(evt: BriscaRoomCreatedEvent) {
        this.cache.putIfAbsent(evt.id, BriscaRoom(evt.id, evt.numberOfPlayers, listOf(evt.creator),
                null, null, emptyList()))
        briscaWebSocketService.sendRoomBriscaStatus(cache[evt.id]!!)
    }

    @EventHandler
    @Throws(RoomNotFoundException::class)
    @Suppress("unused")
    fun on(evt: PlayerJoinedEvent) {
        if (cache.containsKey(evt.id)) {
            // update logic
            val original = this.cache[evt.id]!!
            val updated = original.toBuilder().player(evt.player).build()
            this.cache[evt.id] = updated
            briscaWebSocketService.sendRoomBriscaStatus(cache[evt.id]!!)
            if (updated.numberOfPlayers == updated.players.size) {
                briscaWebSocketService.sendRoomBriscaStarted()
            }
        } else {
            throw RoomNotFoundException(evt.id)
        }
    }

    @EventHandler
    @Throws(RoomNotFoundException::class)
    @Suppress("unused")
    fun on(evt: UserJoinedBriscaRoomEvent) {
        if (cache.containsKey(evt.id)) {
            briscaWebSocketService.sendRoomBriscaStatus(cache[evt.id]!!)
        } else {
            throw RoomNotFoundException(evt.id)
        }
    }

    @EventHandler
    @Suppress("unused")
    fun on(evt: BriscaStartedEvent) {
        val original = this.cache[evt.id]!!
        val updated = original.toBuilder()
                .playerTurn(evt.playerTurn)
                .drivenCard(evt.drivenCard)
                .build()
        this.cache[evt.id] = updated
    }

    @EventHandler
    @Suppress("unused")
    fun on(evt: BriscaCardPlayedEvent) {
        val original = this.cache[evt.id]!!
        val updated = original.toBuilder()
                .round(original.round + PlayerCardRound(evt.player, evt.card))
                .build()
        this.cache[evt.id] = updated
        briscaWebSocketService.sendRoomBriscaCardPlayedEvent(evt)
    }

    @EventHandler
    @Suppress("unused")
    fun on(evt: BriscaPlayerTurnChangedEvent) {
        val original = this.cache[evt.id]!!
        val updated = original.toBuilder().playerTurn(evt.playerTurn).build()
        this.cache[evt.id] = updated
        briscaWebSocketService.sendRoomBriscaPlayerTurnChangedEvent(evt)
    }


    @EventHandler
    @Suppress("unused")
    fun on(evt: BriscaRoundWinnerEvent) {
        val original = this.cache[evt.id]!!
        val updated = original.toBuilder().round(emptyList()).playerTurn(evt.winner).build()
        this.cache[evt.id] = updated
        briscaWebSocketService.sendBriscaRoundWinnerEvent(BriscaRoomRoundWinnerEvent(updated.id, evt.winner, evt.cards))
    }

    @QueryHandler
    @Suppress("unused")
    fun query(query: GetBriscaRoomStatus): BriscaRoom {
        return this.cache.getValue(query.id)
    }

    @QueryHandler
    @Suppress("unused")
    fun query(query: GetBriscaRoomStatuses): Page<BriscaRoom> {
        return PageImpl(this.cache.values.toList(), query.pageable, this.cache.size.toLong())
    }


}