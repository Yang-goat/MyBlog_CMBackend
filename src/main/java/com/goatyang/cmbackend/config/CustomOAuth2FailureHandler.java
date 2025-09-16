package com.goatyang.cmbackend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String redirectUrl = (String) request.getSession().getAttribute("redirect_uri");
        String errorUrl = redirectUrl != null ? redirectUrl : "http://localhost:8080";
        errorUrl += "?error=oauth_failure";
        response.sendRedirect(errorUrl);
    }
}