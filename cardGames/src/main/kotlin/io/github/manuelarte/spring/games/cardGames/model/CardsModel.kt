package io.github.manuelarte.spring.games.cardGames.model

import org.springframework.util.Assert
import java.util.*

data class Card(val suite: Suite, val number: Int)

data class Suite(val name: String)

class CardDeck(private val cards: MutableList<Card>) {

    @Synchronized
    fun getCard(): Card {
        Assert.isTrue(this.cards.size > 0, "Can't get card from deck")
        return this.cards.removeAt(0)
    }

    fun hasNext(): Boolean {
        return this.cards.size > 0
    }

    fun shuffle(seed: Long) {
        cards.shuffle(Random(seed))
    }
}

data class PlayerCardRound(val player: String, val card: Card)


class Util {
    companion object {
        fun spanishDeck(): CardDeck {
            val suites = listOf(Suite("oros"), Suite("copas"), Suite("bastos"), Suite("espadas"))
            return CardDeck(suites.flatMap { suite -> (1..10).map { Pair(suite, it) } }.map { p -> Card(p.first, p
                    .second) }.toMutableList())
        }
    }
}