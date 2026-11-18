package com.starterkit.springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${security.api-key.read:}")
    private String readKey;

    @Value("${security.api-key.admin:}")
    private String adminKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.requireCsrfProtectionMatcher(
                        new AntPathRequestMatcher("/utilizadores/**", "POST")))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .addFilterBefore(new ApiKeyAuthFilter(readKey, adminKey),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint((req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                        .accessDeniedPage("/login")
                )
                .authorizeRequests()
                        // MAIN PAGE + ESTÁTICOS + LOGIN
                        .antMatchers("/", "/index", "/index.html", "/error", "/alunos/**", "/api/alunos/upload-temp").permitAll()
                        .antMatchers("/login", "/logout", "/scan", "/presencas", "/check-in").permitAll()
                        // Dashboard pages require a browser session in LoginRequiredFilter.
                        .antMatchers(HttpMethod.GET, "/dashboard", "/dashboard/**", "/utilizadores", "/utilizadores/**").permitAll()
                        .antMatchers(HttpMethod.POST, "/utilizadores", "/utilizadores/**").permitAll()
                        .antMatchers("/css/**", "/js/**", "/images/**", "/assets/**", "/uploads/**").permitAll()
                        // ADD SWAGGER
                        .antMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
                        .antMatchers("/actuator/health").permitAll()
                        
                        // GET público
                        .antMatchers(HttpMethod.GET, "/api/presencas/**", "/scan").permitAll()

                        // Check-in e presenças públicos
                        .antMatchers(HttpMethod.POST, "/api/check-in", "/api/presencas").permitAll()
                        .antMatchers("/presencas", "/presencas.html").permitAll()

                        // Só ADMIN pode escrever (API)
                        .antMatchers(HttpMethod.PUT, "/api/presencas/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/api/presencas/**").hasRole("ADMIN")

                        .antMatchers("/api/salaveiro/**").hasRole("ADMIN")
                        .anyRequest().authenticated();
        return http.build();
    }
}
