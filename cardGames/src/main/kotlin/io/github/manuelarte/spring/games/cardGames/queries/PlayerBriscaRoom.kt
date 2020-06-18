package io.github.manuelarte.spring.games.cardGames.queries

import io.github.manuelarte.spring.games.cardGames.model.Card
import java.util.*

data class PlayerBriscaRoom(val id: UUID, val player: String, val playerHand: List<Card>) {
    fun toBuilder(): Builder {
        return Builder(id, player, playerHand.toMutableList())
    }

    data class Builder(
        var id: UUID? = null,
        var player: String? = null,
        var playerHand: MutableList<Card>? = null
    ) {
        fun id(id: UUID) = apply { this.id = id }
        fun player(player: String) = apply { this.player = player }
        fun playerHand(playerHand: List<Card>) = apply { this.playerHand = playerHand.toMutableList() }
        fun build() = PlayerBriscaRoom(id!!, player!!, playerHand!!)
    }
}