package com.scrable.bitirme.config;

import com.scrable.bitirme.filter.JwtAuthenticationFilter;
import com.scrable.bitirme.service.UserDetailsServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final UserDetailsServiceImp userDetailsServiceImp;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final CustomLogoutHandler logoutHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
                        JwtAuthenticationFilter jwtAuthenticationFilter)
                        throws Exception {

                return http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(
                                                req -> req
                                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                                .requestMatchers("/login/**", "/register/**", "/admin",
                                                                                "/refresh_token/**",
                                                                                "/verify/**")
                                                                .permitAll()
                                                                .requestMatchers(HttpMethod.GET, "/products",
                                                                                "/products/search", "/products/{id}")
                                                                .permitAll()

                                                                .requestMatchers(HttpMethod.GET, "/orders")
                                                                .hasAuthority("ADMIN")

                                                                .requestMatchers("/cart/**", "/orders/**",
                                                                                "/wishlist/**", "/payments/**",
                                                                                "/address/**")
                                                                .authenticated()
                                                                .requestMatchers(HttpMethod.GET, "/users/{id}")
                                                                .authenticated()
                                                                .requestMatchers("/users", "/users/**")
                                                                .hasAuthority("ADMIN")
                                                                .requestMatchers(HttpMethod.POST, "/products")
                                                                .hasAuthority("ADMIN")
                                                                .requestMatchers(HttpMethod.PUT, "/products/**")
                                                                .hasAuthority("ADMIN")
                                                                .requestMatchers(HttpMethod.DELETE, "/products/**")
                                                                .hasAuthority("ADMIN")

                                                                .anyRequest().authenticated())
                                .userDetailsService(userDetailsServiceImp)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .exceptionHandling(
                                                e -> e.accessDeniedHandler(
                                                                (request, response, accessDeniedException) -> response
                                                                                .setStatus(403))
                                                                .authenticationEntryPoint(new HttpStatusEntryPoint(
                                                                                HttpStatus.UNAUTHORIZED)))
                                .logout(l -> l
                                                .logoutUrl("/logout")
                                                .addLogoutHandler(logoutHandler)
                                                .logoutSuccessHandler(
                                                                (request, response,
                                                                                authentication) -> SecurityContextHolder
                                                                                                .clearContext()))
                                .build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowCredentials(true);
                config.setAllowedOrigins(List.of("http://localhost:3000"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowedMethods(List.of("*"));
                source.registerCorsConfiguration("/**", config);
                return source;
        }

        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
                return configuration.getAuthenticationManager();
        }
}