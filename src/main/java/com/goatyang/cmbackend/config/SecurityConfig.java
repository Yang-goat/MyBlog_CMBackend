package com.goatyang.cmbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * Spring Security 安全配置类
 * 用于配置应用程序的认证、授权、CORS跨域、OAuth2登录等安全相关设置
 */
@Configuration
public class SecurityConfig {

    // 注入自定义的成功处理器
    private final CustomOAuth2SuccessHandler successHandler;

    public SecurityConfig(CustomOAuth2SuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    /**
     * 配置安全过滤链
     * 定义URL访问规则、认证方式、登录/登出行为等核心安全配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF保护（适用于前后端分离架构，通常由前端处理CSRF）
                .csrf(AbstractHttpConfigurer::disable)

                // 配置URL访问授权规则
                .authorizeHttpRequests(auth -> auth
                        // 允许所有用户访问认证相关接口和OAuth2相关接口
                        .requestMatchers("/login/**", "/oauth2/**").permitAll()
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )

                // 配置OAuth2登录相关设置
                .oauth2Login(oauth -> oauth
                        // 使用自定义的 AuthorizationRequestRepository 保存 redirect_uri
                        .authorizationEndpoint(authEndpoint -> authEndpoint
                                .authorizationRequestRepository(authorizationRequestRepository())
                        )
                        // 自定义登录成功处理器
                        .successHandler(successHandler)
                );

        // 构建并返回安全过滤链
        return http.build();
    }

    /**
     * 配置自定义 AuthorizationRequestRepository
     * 用于在 OAuth2 授权请求中保存 redirect_uri
     */
    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new CustomAuthorizationRequestRepository();
    }

    /**
     * 配置CORS跨域过滤器
     * 解决前后端分离架构中的跨域资源共享问题
     */
    @Bean
    public CorsFilter corsFilter() {
        // 创建CORS配置对象
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许的前端源地址（根据实际前端地址修改）
        configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://10.160.27.53:8080/"));
        // 允许的HTTP方法
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 允许的请求头
        configuration.setAllowedHeaders(List.of("*"));
        // 允许携带凭证（如Cookie）
        configuration.setAllowCredentials(true);

        // 注册CORS配置到所有URL路径
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        // 创建并返回CORS过滤器
        return new CorsFilter(source);
    }
}
