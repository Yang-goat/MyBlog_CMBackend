package com.goatyang.cmbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final CustomOAuth2SuccessHandler successHandler;
    private final CustomOAuth2FailureHandler failureHandler;

    public SecurityConfig(CustomOAuth2SuccessHandler successHandler,
                          CustomOAuth2FailureHandler failureHandler) {
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 启用 CORS，使用下面的 CorsConfigurationSource
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 配置 URL 访问规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/login/**", "/oauth2/**").permitAll()
                        .requestMatchers("/admin/**").permitAll()
                        .requestMatchers("/api/auth/me").permitAll()
                        .requestMatchers("/api/comments/article/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 配置 OAuth2 登录
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(authEndpoint -> authEndpoint
                                .authorizationRequestRepository(authorizationRequestRepository())
                        )
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                );

        return http.build();
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new CustomAuthorizationRequestRepository();
    }

    /**
     * Spring Security 内部使用的 CORS 配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许的前端源
        configuration.setAllowedOrigins(List.of(
                "http://localhost:8080",
                "http://10.160.27.53:8080",
                "http://localhost:5173"
        ));

        // 允许的方法
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // 允许的请求头
        configuration.setAllowedHeaders(List.of("*"));

        // 允许携带 Cookie
        configuration.setAllowCredentials(true);

        // 暴露给前端的响应头
        configuration.setExposedHeaders(List.of(
                "Authorization", "Content-Length", "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
