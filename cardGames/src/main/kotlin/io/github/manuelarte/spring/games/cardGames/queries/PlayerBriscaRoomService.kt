package io.github.manuelarte.spring.games.cardGames.queries

import io.github.manuelarte.spring.games.cardGames.commands.api.BriscaCardGivenEvent
import io.github.manuelarte.spring.games.cardGames.commands.api.BriscaCardPlayedEvent
import io.github.manuelarte.spring.games.cardGames.commands.api.BriscaStartedEvent
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class PlayerBriscaRoomService(private val briscaWebSocketService: BriscaWebSocketService) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val cache: MutableMap<UUID, MutableMap<String, PlayerBriscaRoom>> = mutableMapOf()

    @EventHandler
    @Suppress("unused")
    fun on(evt: BriscaStartedEvent) {
        val playersBriscaRoom = evt.players.map { it to PlayerBriscaRoom(evt.id, it, emptyList()) }
                .toMap().toMutableMap()
        this.cache[evt.id] = playersBriscaRoom
        // send event to all the players with their cards
        this.briscaWebSocketService.sendPlayerBriscaStatuses(this.cache[evt.id]!!)

    }

    @EventHandler
    @Suppress("unused")
    fun on(evt: BriscaCardPlayedEvent) {
        val original = this.cache[evt.id]!!
        val updatedPlayerHand = original.getValue(evt.player).playerHand - evt.card
        val updatedPlayerBriscaRoom = original.getValue(evt.player).toBuilder().playerHand(updatedPlayerHand).build()
        original[evt.player] = updatedPlayerBriscaRoom
    }

    @EventHandler
    @Suppress("unused")
    fun on(evt: BriscaCardGivenEvent) {
        val original = this.cache.getOrDefault(evt.id, emptyMap<String, PlayerBriscaRoom>()).toMutableMap()
        val updatedPlayerHand = original[evt.player]!!.playerHand + evt.card
        original[evt.player] = original[evt.player]!!.toBuilder().playerHand(updatedPlayerHand).build()
        cache[evt.id] = original
        briscaWebSocketService.sendPlayerBriscaCardGivenEvent(evt)
    }

    @QueryHandler
    @Suppress("unused")
    fun query(query: GetRoomBriscaPlayerStatus): PlayerBriscaRoom {
        return this.cache[query.id]!![query.player]!!
    }

}