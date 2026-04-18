package com.addressbook.config;

import com.addressbook.filter.JwtAuthFilter;
import com.addressbook.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * Spring Security 6 — stateless JWT configuration.
 *
 * CORS STRATEGY (two-layer approach):
 * ────────────────────────────────────
 * Layer 1 — corsFilter() bean annotated @Order(Ordered.HIGHEST_PRECEDENCE)
 *            This is a raw servlet filter that runs BEFORE Spring Security's
 *            filter chain.  It handles OPTIONS preflight requests and adds
 *            Access-Control-Allow-* headers to every response.  This is what
 *            fixes the "blocked by CORS policy" error on Elastic Beanstalk,
 *            where the OPTIONS request was reaching the container before
 *            Spring Security could respond to it.
 *
 * Layer 2 — http.cors(…) inside filterChain()
 *            Keeps Spring Security aware of the same CORS policy so that
 *            authenticated requests also carry the correct headers.
 *
 * DO NOT delete CorsConfig.java if it still uses a CorsFilter bean —
 * instead REPLACE it with this file entirely. Having two CorsFilter beans
 * causes duplicate headers and filter ordering conflicts.
 *
 * PREREQUISITES:
 *   • Delete the old CorsConfig.java (it registered a conflicting CorsFilter).
 *   • Add the 4 Maven deps from POM_ADDITIONS.xml to pom.xml.
 *   • Keep app.jwt.* properties in application.properties.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // ── Password encoder ────────────────────────────────────────────────────
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ── Authentication provider ─────────────────────────────────────────────
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    // ── Authentication manager (needed by AuthController) ───────────────────
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    // ── CORS configuration source (shared by both layers) ───────────────────
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // Allow all origins — tighten to specific domains in production:
        //   cfg.setAllowedOrigins(List.of("http://localhost:4200", "https://yourapp.com"));
        cfg.setAllowedOriginPatterns(List.of("*"));

        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        cfg.setAllowedHeaders(List.of("*"));

        // Must be false when allowedOriginPatterns("*") is used.
        // Set to true only if you also restrict to specific origins.
        cfg.setAllowCredentials(false);

        // Cache preflight response for 1 hour (3600 seconds)
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    /**
     * Layer 1 — Highest-precedence CORS filter.
     *
     * @Order(Ordered.HIGHEST_PRECEDENCE) ensures this filter runs at position
     * Integer.MIN_VALUE in the servlet filter chain — before Spring Security's
     * DelegatingFilterProxy (which runs at position -100).
     *
     * Effect: OPTIONS preflight requests receive CORS headers and a 200 OK
     * immediately, without ever reaching Spring Security.  This is the fix for:
     *   "Response to preflight request doesn't pass access control check:
     *    No 'Access-Control-Allow-Origin' header is present"
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }

    // ── Security filter chain ───────────────────────────────────────────────
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — stateless REST API, no session
            .csrf(AbstractHttpConfigurer::disable)

            // Layer 2 — keep Spring Security CORS-aware using same source
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // No HttpSession — JWT is the sole auth mechanism
            .sessionManagement(sm ->
                    sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                // ── Public: auth ──────────────────────────────────────────
                .requestMatchers("/api/auth/**").permitAll()

                // ── Public: Swagger / OpenAPI ─────────────────────────────
                .requestMatchers(
                        "/api-docs",
                        "/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                ).permitAll()

                // ── Public: OPTIONS pre-flight (belt-and-suspenders) ──────
                // The corsFilter() above already handles these, but this rule
                // ensures Spring Security never blocks a preflight with a 401.
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ── Everything else requires a valid JWT ──────────────────
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
