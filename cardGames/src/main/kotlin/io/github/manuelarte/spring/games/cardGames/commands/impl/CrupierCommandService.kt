package io.github.manuelarte.spring.games.cardGames.commands.impl

import io.github.manuelarte.spring.games.cardGames.commands.api.*
import io.github.manuelarte.spring.games.cardGames.model.Card
import io.github.manuelarte.spring.games.cardGames.model.CardDeck
import io.github.manuelarte.spring.games.cardGames.model.Util
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class CrupierCommandService(private val commandGateway: CommandGateway) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val cacheRoom: MutableMap<UUID, CrupierRoomProjector> = mutableMapOf()
    private val cacheCardDeck: MutableMap<UUID, CardDeck> = mutableMapOf()
    private val cache: MutableMap<UUID, CrupierBriscaProjector> = mutableMapOf()

    @EventHandler
    @Suppress("unused")
    fun on(evt: BriscaRoomCreatedEvent) {
        log.debug("CrupierCommandService handling BriscaRoomCreatedEvent: {}", evt)
        this.cacheRoom.putIfAbsent(evt.id, CrupierRoomProjector(evt.id, evt.seed, evt.numberOfPlayers, listOf(evt.creator)))
    }

    @EventHandler
    @Suppress("unused")
    fun on(evt: PlayerJoinedEvent) {
        log.debug("CrupierCommandService handling PlayerJoinedEvent: {}", evt)
        val original = this.cacheRoom[evt.id]!!
        val updated = original.toBuilder().player(evt.player).build()
        this.cacheRoom[evt.id] = updated
        if (updated.isGameReady()) {
            val cardDeck = Util.spanishDeck().also {
                it.shuffle(updated.seed)
            }
            val drivenCard = cardDeck.getCard()
            cacheCardDeck[evt.id] = cardDeck
            val cmd = BriscaStartedCommand(evt.id, updated.players[0], updated.players, drivenCard)
            commandGateway.send<BriscaStartedCommand>(cmd)
        }
    }

    @EventHandler
    @Suppress("unused")
    fun on(evt: BriscaStartedEvent) {
        log.debug("CrupierCommandService handling BriscaStartedEvent: {}", evt)
        val cardDeck = this.cacheCardDeck[evt.id]!!
        this.cache.putIfAbsent(evt.id, CrupierBriscaProjector(evt.id, evt.players, evt.drivenCard, cardDeck))
        repeat(3) {
            for (player in evt.players) {
                val card = cardDeck.getCard()
                val briscaCardGivenCommand = BriscaGiveCardCommand(evt.id, player, card)
                log.debug("Sending BriscaCardGivenCommand: {}", briscaCardGivenCommand)
                commandGateway.send<BriscaGiveCardCommand>(briscaCardGivenCommand)
            }
        }

    }

    @EventHandler
    @Suppress("unused")
    fun on(evt: BriscaRoundWinnerEvent) {
        log.debug("CrupierCommandService handling BriscaRoundWinnerEvent: {}", evt)
        val original = this.cache[evt.id]!!
        val numberOfPlayers = original.players.size
        for (i in original.players.indices) {
            val offset = original.players.indexOf(evt.winner)
            val player = original.players[(i + offset) % numberOfPlayers]
            val newCard = if (original.cardDeck.hasNext()) original.cardDeck.getCard() else original.drivenCard
            val briscaCardGivenEvent = BriscaGiveCardCommand(evt.id, player, newCard)
            log.debug("Sending BriscaCardGivenCommand: {}", briscaCardGivenEvent)
            commandGateway.send<BriscaGiveCardCommand>(briscaCardGivenEvent)
        }

    }

    private data class CrupierRoomProjector(val id: UUID, val seed: Long, val numberOfPlayers: Int,
                                            val players: List<String>) {

        fun isGameReady() = this.numberOfPlayers == this.players.size

        fun toBuilder() = Builder(id, seed, numberOfPlayers, players.toMutableList())

        data class Builder(
                var id: UUID? = null,
                var seed: Long? = null,
                var numberOfPlayers: Int? = null,
                var players: MutableList<String>? = null) {

            fun id(id: UUID) = apply { this.id = id }
            fun seed(seed: Long) = apply { this.seed = seed }
            fun players(players: List<String>) = apply { this.players = players.toMutableList() }
            fun player(player: String): Builder {
                if (this.players == null) {
                    this.players = mutableListOf()
                }
                this.players!!.add(player)
                return this
            }
            fun build() = CrupierRoomProjector(id!!, seed!!, numberOfPlayers!!, players!!)
        }
    }

    private data class CrupierBriscaProjector(val id: UUID, val players: List<String>,
                                      val drivenCard: Card, val cardDeck: CardDeck) {

        fun toBuilder(): Builder {
            return Builder(id, players, drivenCard, cardDeck)
        }

        data class Builder(
                var id: UUID? = null,
                var players: List<String>? = null,
                var drivenCard: Card?, var cardDeck: CardDeck) {

            fun id(id: UUID) = apply { this.id = id }
            fun players(players: List<String>) = apply { this.players = players }
            fun drivenCard(drivenCard: Card?) = apply { this.drivenCard = drivenCard }
            fun cardDeck(cardDeck: CardDeck) = apply { this.cardDeck = cardDeck }
            fun build() = CrupierBriscaProjector(id!!, players!!, drivenCard!!, cardDeck)
        }
    }

}