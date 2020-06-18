package io.github.manuelarte.spring.games.cardGames.games

import io.github.manuelarte.spring.games.cardGames.model.Card
import io.github.manuelarte.spring.games.cardGames.model.PlayerCardRound
import org.springframework.util.Assert

class BriscaEngineGameRule: GameRule {

    fun roundWinner(round: List<PlayerCardRound>, drivenCard: Card): String {
        Assert.isTrue(round.size > 1, "Not all the players played the cards")
        var currentWinnerPlayerCard = round[0]
        for (i in 1 until round.size) {
            val otherPlayerCard = round[i]
            if (currentWinnerPlayerCard.card.suite == otherPlayerCard.card.suite) {
                currentWinnerPlayerCard = getBiggerNumber(currentWinnerPlayerCard, otherPlayerCard)
            } else if (otherPlayerCard.card.suite == drivenCard.suite) {
                currentWinnerPlayerCard = otherPlayerCard
            }

        }
        return currentWinnerPlayerCard.player
    }

    private fun getBiggerNumber(first: PlayerCardRound, second: PlayerCardRound): PlayerCardRound {
        // assuming same suite
        if (first.card.number == 1) return first
        if (second.card.number == 1) return second
        if (first.card.number == 3) return first
        if (second.card.number == 3) return second
        return if (first.card.number > second.card.number) first else second
    }

}