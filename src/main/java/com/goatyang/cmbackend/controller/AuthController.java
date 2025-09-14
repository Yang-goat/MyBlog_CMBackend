package com.goatyang.cmbackend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证相关接口控制器
 * 负责处理用户登录状态查询、登录引导、登出等认证相关HTTP请求
 * 接口路径统一前缀：/api/auth
 */
@RestController // 标识该类为REST风格控制器，返回JSON格式响应
@RequestMapping("/api/auth") // 定义所有接口的基础路径
public class AuthController {

    /**
     * 获取当前登录用户的信息
     * 用于前端获取已登录用户的基本资料（如用户名、头像、GitHub信息等）
     *
     * @param authentication Spring Security 认证对象，包含用户登录状态信息
     * @param oAuth2User 注入当前登录的OAuth2用户对象（由@AuthenticationPrincipal自动获取）
     * @return 包含用户信息的Map（登录时返回用户详情，未登录时返回错误提示）
     */
    @GetMapping("/me") // 处理GET请求，路径：/api/auth/me
    public Map<String, Object> getCurrentUser(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        // 1. 校验用户登录状态：未登录/匿名登录时返回错误提示
        if (authentication == null ||  // 认证对象为空（无登录上下文）
                !authentication.isAuthenticated() ||  // 认证未通过
                authentication instanceof AnonymousAuthenticationToken) {  // 匿名用户（未登录）
            return Map.of("error", "未登录"); // 返回未登录错误信息
        }

        // 2. 若存在OAuth2用户信息（GitHub登录成功），提取并返回用户详情
        if (oAuth2User != null) {
            Map<String, Object> userInfo = new HashMap<>();
            // 从OAuth2用户属性中提取关键信息（属性名与GitHub返回字段对应）
            userInfo.put("username", oAuth2User.getAttribute("login")); // GitHub登录用户名（唯一）
            userInfo.put("avatar", oAuth2User.getAttribute("avatar_url")); // 用户头像URL
            return userInfo; // 返回组装后的用户信息
        }

        // 3. 其他异常情况（如登录状态存在但无OAuth2用户信息）
        return Map.of("error", "无法获取用户信息");
    }
}