package io.github.manuelarte.spring.games.cardGames.config

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class AuthenticationChannelInterceptorAdapter(val jwtDecoder: JwtDecoder): ChannelInterceptor {

    @Override
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {

        val accessor: StompHeaderAccessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)!!

        val nativeHeaders = accessor.messageHeaders["nativeHeaders"] as? LinkedMultiValueMap<String, String>
        if (nativeHeaders?.contains("X-Authorization") == true) {
            val idToken = nativeHeaders["X-Authorization"]!![0].substring("Bearer ".length)
            val tokenAuth = JwtAuthenticationToken(jwtDecoder.decode(idToken))
            SecurityContextHolder.getContext().authentication = tokenAuth
        }

        return message
    }
}