package io.github.manuelarte.spring.games.cardGames.commands.api

import io.github.manuelarte.spring.games.cardGames.model.Card
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class JoinPlayerCommand(@TargetAggregateIdentifier val id: UUID, val player: String)

/* Brisca commands */
data class CreateBriscaRoomCommand(@TargetAggregateIdentifier val id: UUID, val seed: Long, val creator: String,
                                   val numberOfPlayers: Int)

data class BriscaPlayCardCommand(@TargetAggregateIdentifier val id: UUID, val player: String, val card: Card)

data class BriscaStartedCommand(@TargetAggregateIdentifier val id: UUID, val playerTurn: String,
                                val players: List<String>, val drivenCard: Card)

data class BriscaGiveCardCommand(@TargetAggregateIdentifier val id: UUID, val player: String, val card: Card)

data class BriscaRoundWinnerCommand(@TargetAggregateIdentifier val id: UUID, val winner: String, val cards: List<Card>)

data class BriscaChangePlayerTurnCommand(@TargetAggregateIdentifier val id: UUID, val playerTurn: String)