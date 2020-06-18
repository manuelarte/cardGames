package io.github.manuelarte.spring.games.cardGames.queries

import io.github.manuelarte.spring.games.cardGames.model.Card
import io.github.manuelarte.spring.games.cardGames.model.PlayerCardRound
import java.util.*

/**
 * Projector with all the information not dependent on players playing the game
 */
data class BriscaRoom(val id: UUID, val numberOfPlayers: Int, val players: List<String>, val drivenCard: Card?,
                      val playerTurn: String?, val round: List<PlayerCardRound>) {

    fun toBuilder(): Builder {
        return Builder(id, numberOfPlayers, players.toMutableList(), drivenCard, playerTurn, round.toMutableList())
    }

    data class Builder(
            var id: UUID? = null,
            var numberOfPlayers: Int? = null,
            var players: MutableList<String>? = null,
            var drivenCard: Card?,
            var playerTurn: String?,
            var round: MutableList<PlayerCardRound>? = null) {

        fun id(id: UUID) = apply { this.id = id }
        fun numberOfPlayers(numberOfPlayers: Int) = apply { this.numberOfPlayers = numberOfPlayers }
        fun players(players: List<String>) = apply { this.players = players.toMutableList() }
        fun player(player: String): Builder {
            if (this.players == null) {
                this.players = mutableListOf()
            }
            this.players!! += player
            return this
        }
        fun drivenCard(drivenCard: Card?) = apply { this.drivenCard = drivenCard }
        fun playerTurn(playerTurn: String?) = apply {this.playerTurn = playerTurn}
        fun round(round: List<PlayerCardRound>) = apply { this.round = round.toMutableList() }
        fun addCardRound(playerCardRound: PlayerCardRound): Builder {
            if (this.round == null) {
                this.round = mutableListOf()
            }
            this.round!! += playerCardRound
            return this
        }
        fun build() = BriscaRoom(id!!, numberOfPlayers!!, players ?: emptyList(), drivenCard, playerTurn,
                round ?: emptyList())
    }
}