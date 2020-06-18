package io.github.manuelarte.spring.games.cardGames

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CardGamesApplication

fun main(args: Array<String>) {
    runApplication<CardGamesApplication>(*args)
}