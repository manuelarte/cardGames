package io.github.manuelarte.spring.games.cardGames.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder


@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
class SecurityConfig(@Value("\${auth0.audience}") private val audience: String,
                     @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}") private val issuer: String)
    : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    public override fun configure(http: HttpSecurity) {
        http.cors()
                .and()
                    .oauth2ResourceServer().jwt()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return (JwtDecoders.fromOidcIssuerLocation(issuer) as NimbusJwtDecoder).apply {
            setJwtValidator(DelegatingOAuth2TokenValidator(JwtValidators.createDefaultWithIssuer(issuer),
                    AudienceValidator(audience)))
        }
    }
}