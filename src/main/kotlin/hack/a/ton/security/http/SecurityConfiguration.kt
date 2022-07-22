package hack.a.ton.security.http

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.util.matcher.AndRequestMatcher
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

//    @Bean
//    @Order(Ordered.LOWEST_PRECEDENCE)
//    fun defaultSecurityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
//        return httpSecurity.cors().and()
//            .exceptionHandling().authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)).and()
//            .requestMatchers().anyRequest().and()
//            .authorizeRequests().anyRequest().authenticated().and()
//            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//            .formLogin().disable()
//            .logout().disable()
//            .httpBasic().disable()
//            .anonymous().disable()
//            .csrf().disable()
//            .build()
//    }
//
    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE - 1)
    fun actuatorFilterChain(
        httpSecurity: HttpSecurity,
        @Value("\${management.server.port}") managementPort: Int
    ): SecurityFilterChain {
        httpSecurity
            .requestMatchers().anyRequest().and()
            .authorizeRequests().anyRequest().permitAll().and()
            .sessionManagement().disable()
            .formLogin().disable()
            .logout().disable()
            .httpBasic().disable()
            .csrf().disable()
            .cors().disable()

        return httpSecurity.build()
    }
}
