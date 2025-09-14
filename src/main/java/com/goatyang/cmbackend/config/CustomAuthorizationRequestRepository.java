package com.goatyang.cmbackend.config;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        // 从session获取授权请求
        return (OAuth2AuthorizationRequest) request.getSession().getAttribute("authorization_request");
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        // 保存授权请求和redirect_uri到session
        request.getSession().setAttribute("authorization_request", authorizationRequest);
        
        String redirectUri = request.getParameter("redirect_uri");
        System.out.println("redirectUri" + redirectUri);
        if (redirectUri != null) {
            request.getSession().setAttribute("redirect_uri", redirectUri);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        // 移除并返回授权请求
        OAuth2AuthorizationRequest authRequest = this.loadAuthorizationRequest(request);
        request.getSession().removeAttribute("authorization_request");
        return authRequest;
    }
}