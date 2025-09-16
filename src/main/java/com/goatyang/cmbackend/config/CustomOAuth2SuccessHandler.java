package com.goatyang.cmbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goatyang.cmbackend.model.User;
import com.goatyang.cmbackend.repository.UserRepository;
import com.goatyang.cmbackend.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // 用于写 JSON

    public CustomOAuth2SuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        System.out.println("CustomOAuth2SuccessHandler onAuthenticationSuccess");

        // 获取 GitHub 用户信息
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // 先获取GitHub返回的id（实际是Integer），通过Number安全转为Long
        Number githubIdNum = (Number) oAuth2User.getAttributes().get("id");
        Long githubId = githubIdNum.longValue(); // 安全转换为Long，避免ClassCastException

        // 查询数据库
        Optional<User> userOpt = userRepository.findByGithubId(githubId);

        if (userOpt.isEmpty()) {
            // 用户不存在 → 返回统一 JSON 错误响应
            ApiResponse<Void> apiResponse = ApiResponse.error(401, "用户未注册，拒绝访问");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
            return;
        }

        // 用户存在 → 跳转回登录时的页面
        String redirectUri = (String) request.getSession().getAttribute("redirect_uri");
        String targetUrl = redirectUri != null ? redirectUri : "http://localhost:8080/";
        System.out.println("登录成功，跳转到: " + targetUrl);

        response.sendRedirect(targetUrl);
    }
}
