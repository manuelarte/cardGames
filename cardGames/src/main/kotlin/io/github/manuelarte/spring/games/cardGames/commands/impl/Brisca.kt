package io.github.manuelarte.spring.games.cardGames.commands.impl

import io.github.manuelarte.spring.games.cardGames.commands.api.*
import io.github.manuelarte.spring.games.cardGames.model.Card
import io.github.manuelarte.spring.games.cardGames.model.PlayerCardRound
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import org.slf4j.LoggerFactory
import org.springframework.util.Assert
import java.util.*

@Aggregate
internal class Brisca() {

    private val log = LoggerFactory.getLogger(javaClass)

    @AggregateIdentifier
    private lateinit var id: UUID

    private var numberOfPlayers: Int = 0
    private lateinit var playerTurn: String
    private lateinit var players: List<String>
    private lateinit var playersHands: Map<String, List<Card>>
    private lateinit var round: List<PlayerCardRound>

    @CommandHandler
    constructor(cmd: CreateBriscaRoomCommand): this() {
        AggregateLifecycle
                .apply(BriscaRoomCreatedEvent(cmd.id, cmd.seed, cmd.creator, cmd.numberOfPlayers))
    }

    @CommandHandler
    @Suppress("unused")
    fun handle(cmd: JoinPlayerCommand) {
        AggregateLifecycle.apply(PlayerJoinedEvent(cmd.id, cmd.player))
    }

    @CommandHandler
    @Suppress("unused")
    fun handle(cmd: BriscaStartedCommand) {
        AggregateLifecycle.apply(BriscaStartedEvent(cmd.id, cmd.playerTurn, cmd.players, cmd.drivenCard))
    }

    @CommandHandler
    @Suppress("unused")
    fun handle(cmd: BriscaPlayCardCommand) {
        AggregateLifecycle.apply(BriscaCardPlayedEvent(cmd.id, cmd.player, cmd.card))
    }

    @CommandHandler
    @Suppress("unused")
    fun handle(cmd: BriscaChangePlayerTurnCommand) {
        AggregateLifecycle.apply(BriscaPlayerTurnChangedEvent(cmd.id, this.playerTurn))
    }

    @CommandHandler
    @Suppress("unused")
    fun handle(cmd: BriscaRoundWinnerCommand) {
        AggregateLifecycle.apply(BriscaRoundWinnerEvent(cmd.id, cmd.winner, cmd.cards))
    }

    @CommandHandler
    @Suppress("unused")
    fun handle(cmd: BriscaGiveCardCommand) {
        AggregateLifecycle.apply(BriscaCardGivenEvent(cmd.id, cmd.player, cmd.card))
    }

    @EventSourcingHandler
    @Suppress("unused")
    fun on(evt: BriscaRoomCreatedEvent) {
        log.debug("Handling BriscaRoomCreatedEvent: {}", evt)
        Assert.isTrue(evt.numberOfPlayers > 1, "Number of players invalid")
        this.id = evt.id
        this.numberOfPlayers = evt.numberOfPlayers
        this.players = listOf(evt.creator)
    }

    @EventSourcingHandler
    @Suppress("unused")
    fun on(evt: PlayerJoinedEvent) {
        log.debug("Handling PlayerJoinedEvent: {}", evt)
        Assert.isTrue(!this.players.contains(evt.player), "Player ${evt.player} already in the game")
        this.players = this.players + evt.player
    }

    @EventSourcingHandler
    @Suppress("unused")
    fun on(evt: BriscaStartedEvent) {
        log.debug("Handling BriscaStartedEvent: {}", evt)
        this.playerTurn = evt.playerTurn
        this.playersHands = emptyMap()
        this.round = emptyList()
    }

    @EventSourcingHandler
    @Suppress("unused")
    fun on(evt: BriscaCardPlayedEvent) {
        log.debug("Handling BriscaCardPlayedEvent: {}", evt)
        Assert.isTrue(this.playerTurn == evt.player, "Not player's ${evt.player} turn")
        Assert.isTrue(this.playersHands.getValue(this.playerTurn).contains(evt.card), "Card not in player's hand")
        // update player's turn
        this.playersHands = this.playersHands.toMutableMap().also {
            it[this.playerTurn] = this.playersHands.getValue(this.playerTurn) - evt.card
        }
        this.round = this.round + PlayerCardRound(evt.player, evt.card)
        this.playerTurn = this.players[(this.players.indexOf(this.playerTurn) + 1) % this.numberOfPlayers]
    }

    @EventSourcingHandler
    @Suppress("unused")
    fun on(evt: BriscaPlayerTurnChangedEvent) {
        Assert.isTrue(this.playerTurn == evt.playerTurn, "Not player's ${evt.playerTurn} turn")
    }

    @EventSourcingHandler
    @Suppress("unused")
    fun on(evt: BriscaRoundWinnerEvent) {
        this.round = emptyList()
        this.playerTurn = evt.winner
    }

    @EventSourcingHandler
    @Suppress("unused")
    fun on(evt: BriscaCardGivenEvent) {
        this.playersHands = this.playersHands.toMutableMap().also {
            it[evt.player] = this.playersHands.getOrDefault(evt.player, emptyList()) + evt.card
        }
    }


}