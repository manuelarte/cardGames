package io.github.manuelarte.spring.games.cardGames.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.StompEndpointRegistry





@Configuration
class WebSocketSecurityConfig: AbstractSecurityWebSocketMessageBrokerConfigurer() {

    override fun configureInbound(messages: MessageSecurityMetadataSourceRegistry) {
        //messages.simpDestMatchers("/app/game/**/player/**").authenticated()
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/brisca").setAllowedOrigins("*").withSockJS()
    }

    override fun sameOriginDisabled() = true

}