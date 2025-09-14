package com.goatyang.cmbackend.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        System.out.println("CustomOAuth2SuccessHandler onAuthenticationSuccess");

        String redirectUri = (String) request.getSession().getAttribute("redirect_uri");
        String targetUrl = redirectUri != null ? redirectUri : "http://localhost:8080/";
        
        System.out.println("targetUrl: " + targetUrl);
        response.sendRedirect(targetUrl);
    }
}
