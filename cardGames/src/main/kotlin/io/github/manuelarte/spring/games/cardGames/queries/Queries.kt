package io.github.manuelarte.spring.games.cardGames.queries

import org.springframework.data.domain.Pageable
import java.util.*

data class GetBriscaRoomStatus(val id: UUID)
data class GetBriscaRoomStatuses(val pageable: Pageable)

data class GetRoomBriscaPlayerStatus(val id: UUID, val player: String)