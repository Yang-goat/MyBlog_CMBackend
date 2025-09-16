package com.goatyang.cmbackend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * 获取当前登录用户的信息
     * 前端 fetch 时不会被重定向
     */
    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        // 未登录或匿名用户
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "未登录");
            response.put("loginUrl", "/oauth2/authorization/github"); // 前端可用此 URL 引导登录
            return response;
        }

        // 已登录且存在 OAuth2 用户信息
        if (oAuth2User != null) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", oAuth2User.getAttribute("login"));
            userInfo.put("avatar", oAuth2User.getAttribute("avatar_url"));
            userInfo.put("githubid", oAuth2User.getAttribute("id"));
            return userInfo;
        }

        // 已登录但无法获取 OAuth2 用户信息
        return Map.of("error", "无法获取用户信息");
    }
}
