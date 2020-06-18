package io.github.manuelarte.spring.games.cardGames.commands.impl

import io.github.manuelarte.spring.games.cardGames.commands.api.BriscaCardPlayedEvent
import io.github.manuelarte.spring.games.cardGames.commands.api.BriscaChangePlayerTurnCommand
import io.github.manuelarte.spring.games.cardGames.commands.api.BriscaRoundWinnerCommand
import io.github.manuelarte.spring.games.cardGames.commands.api.BriscaStartedEvent
import io.github.manuelarte.spring.games.cardGames.games.BriscaEngineGameRule
import io.github.manuelarte.spring.games.cardGames.model.Card
import io.github.manuelarte.spring.games.cardGames.model.PlayerCardRound
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class RoundCommandService(private val commandGateway: CommandGateway) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val cache: MutableMap<UUID, RoundProjector> = mutableMapOf()
    private val briscaEngineGameRule = BriscaEngineGameRule()

    @EventHandler
    @Suppress("unused")
    fun on(evt: BriscaStartedEvent) {
        log.debug("RoundCommandService handling BriscaStartedEvent: {}", evt)
        this.cache.putIfAbsent(evt.id, RoundProjector(evt.id, evt.players.size, evt.playerTurn, emptyList(), evt
                .drivenCard))
    }

    @EventHandler
    @Suppress("unused")
    fun on(evt: BriscaCardPlayedEvent) {
        log.debug("RoundCommandService handling BriscaCardPlayedEvent: {}", evt)
        val original = this.cache[evt.id]!!
        val updated = original.toBuilder().addCardRound(PlayerCardRound(evt.player, evt.card)).build()
        this.cache[evt.id] = updated

        if (updated.isRoundReady()) {
            val roundWinner = briscaEngineGameRule.roundWinner(updated.round, updated.drivenCard)
            val cmd = BriscaRoundWinnerCommand(updated.id, roundWinner, updated.round.map { it.card })
            log.debug("Sending BriscaRoundWinnerCommand: {}", cmd)
            commandGateway.send<BriscaRoundWinnerCommand>(cmd).thenApply {
                this.cache[evt.id] = this.cache[evt.id]!!.clearRound()
            }
        } else {
            //val original = this.cache[evt.id]!!
            //val newPlayerTurn = this.players[(this.players.indexOf(this.playerTurn) + 1) % this.numberOfPlayers]
            ///val updated = original.toBuilder().playerTurn().build()
            //this.cache[evt.id] = updated
            commandGateway.send<BriscaChangePlayerTurnCommand>(BriscaChangePlayerTurnCommand(evt.id, ""))
        }


    }

    private data class RoundProjector(val id: UUID, val numberOfPlayers: Int, val playerTurn: String,
                                      val round: List<PlayerCardRound>, val drivenCard: Card) {

        fun toBuilder(): Builder {
            return Builder(id, numberOfPlayers, playerTurn, round.toMutableList(), drivenCard)
        }

        fun isRoundReady() = numberOfPlayers == round.size

        fun clearRound() = toBuilder().round(emptyList()).build()

        data class Builder(
                var id: UUID? = null,
                var numberOfPlayers: Int? = null,
                var playerTurn: String? = null,
                var round: MutableList<PlayerCardRound>? = null,
                var drivenCard: Card?) {

            fun id(id: UUID) = apply { this.id = id }
            fun numberOfPlayers(numberOfPlayers: Int) = apply { this.numberOfPlayers = numberOfPlayers }
            fun playerTurn(playerTurn: String) = apply { this.playerTurn = playerTurn }
            fun round(round: List<PlayerCardRound>) = apply { this.round = round.toMutableList() }
            fun addCardRound(playerCardRound: PlayerCardRound): Builder {
                if (this.round == null) {
                    this.round = mutableListOf()
                }
                this.round!! += playerCardRound
                return this
            }
            fun drivenCard(drivenCard: Card?) = apply { this.drivenCard = drivenCard }
            fun build() = RoundProjector(id!!, numberOfPlayers!!, playerTurn!!, round ?: emptyList(), drivenCard!!)
        }
    }

}