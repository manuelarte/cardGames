package io.github.manuelarte.spring.games.cardGames.controllers

import io.github.manuelarte.spring.games.cardGames.commands.api.CreateBriscaRoomCommand
import io.github.manuelarte.spring.games.cardGames.commands.api.JoinPlayerCommand
import io.github.manuelarte.spring.games.cardGames.controllers.dto.BriscaRoomCreatedDto
import io.github.manuelarte.spring.games.cardGames.controllers.dto.RoomBriscaStatusDto
import io.github.manuelarte.spring.games.cardGames.queries.BriscaRoom
import io.github.manuelarte.spring.games.cardGames.queries.GetBriscaRoomStatus
import io.github.manuelarte.spring.games.cardGames.queries.GetBriscaRoomStatuses
import io.github.manuelarte.spring.games.cardGames.util.PageResponseType
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping("api/v1/games/brisca")
class BriscaController(private val queryGateway: QueryGateway, private val commandGateway: CommandGateway) {

    @GetMapping
    fun getBriscaRooms(@PageableDefault pageable: Pageable): CompletableFuture<Page<RoomBriscaStatusDto>> {
        return queryGateway.query(GetBriscaRoomStatuses(pageable), PageResponseType(BriscaRoom::class.java))
                .thenApply { it!!.map { p -> RoomBriscaStatusDto(p.id, p.numberOfPlayers, p.players,
                        "/brisca", "/topic/games/brisca/${p.id}") }
        }
    }

    @GetMapping("/{id}")
    fun getBriscaRoom(@PathVariable id: UUID): CompletableFuture<RoomBriscaStatusDto> {
        return queryGateway.query(GetBriscaRoomStatus(id), BriscaRoom::class.java)
                .thenApply { p -> RoomBriscaStatusDto(p.id, p.numberOfPlayers, p.players,
                        "/brisca", "/topic/games/brisca/${p.id}")
                }
    }

    @PostMapping
    @Secured
    fun createGame(): CompletableFuture<BriscaRoomCreatedDto> {
        val id = UUID.randomUUID()
        val creator =  getAuthName()
        return commandGateway.send<UUID>(CreateBriscaRoomCommand(id, 2, creator, 2))
                .thenApply { BriscaRoomCreatedDto(id ,"/brisca", "/topic/games/brisca/$id",
                        2, listOf(creator)) }
    }

    @PostMapping("/{id}")
    @Secured
    fun joinGame(@PathVariable id: UUID): CompletableFuture<RoomBriscaStatusDto> {
        return commandGateway.send<UUID>(JoinPlayerCommand(id, getAuthName()))
                .thenCompose { this.getBriscaRoom(id) }
    }

    private fun getAuthName() = SecurityContextHolder.getContext().authentication.name

}