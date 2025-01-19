package me.matthewe.devroomtrialbackend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())  // Disable CSRF for REST APIs
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/auth/**", "/accounts/**", "/health").permitAll()  // Allow /auth/** endpoints
//                        .anyRequest().authenticated()  // Secure all other endpoints
//                )
//                .httpBasic(httpBasic -> httpBasic.disable())  // Disable Basic Auth
//                .formLogin(formLogin -> formLogin.disable());  // Disable Form Login
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable()); // Disable CSRF for development
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
