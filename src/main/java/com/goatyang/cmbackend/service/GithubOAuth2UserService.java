package com.goatyang.cmbackend.service;

import com.goatyang.cmbackend.model.CustomOAuth2User;
import com.goatyang.cmbackend.model.User;
import com.goatyang.cmbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * GitHub OAuth2用户服务类
 * 扩展Spring Security的默认OAuth2用户服务，实现GitHub登录用户与本地用户系统的关联
 * 负责处理GitHub OAuth2认证后的用户信息同步与本地用户创建/更新
 */
@Service
@RequiredArgsConstructor // 使用Lombok生成构造方法，注入依赖
public class GithubOAuth2UserService extends DefaultOAuth2UserService {

    // 注入用户仓库，用于用户信息的数据库操作
    private final UserRepository userRepository;

    /**
     * 加载并处理OAuth2用户信息
     * 重写父类方法，实现自定义的用户信息处理逻辑
     *
     * @param userRequest OAuth2用户请求，包含认证信息
     * @return 自定义的OAuth2User对象，包含本地用户ID
     * @throws OAuth2AuthenticationException 当认证过程出现异常时抛出
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 调用父类的loadUser方法，获取GitHub返回的原始用户信息
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 提取GitHub返回的用户属性信息（如ID、用户名、头像等）
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 将GitHub用户信息映射为本地系统的User实体，并关联访问令牌
        User user = mapGithubUserToLocalUser(attributes, userRequest.getAccessToken().getTokenValue());

        // 保存或更新用户信息到数据库（如果是新用户则创建，已有用户则更新）
        userRepository.save(user);

        // 返回自定义的OAuth2User对象，包含本地数据库中的用户ID和GitHub用户属性
        // "login"指定了用于获取用户名的属性键（对应GitHub返回的login字段）
        return new CustomOAuth2User(user.getId(), attributes, "login");
    }

    /**
     * 将GitHub用户信息映射到本地User实体
     * 实现第三方用户信息与本地用户系统的同步逻辑
     *
     * @param githubAttributes GitHub返回的用户属性集合
     * @param accessToken OAuth2访问令牌，用于后续可能的GitHub API调用
     * @return 映射后的本地User实体（新用户或更新后的已有用户）
     */
    private User mapGithubUserToLocalUser(Map<String, Object> githubAttributes, String accessToken) {
        // 从GitHub响应中提取必要的用户信息
        // GitHub返回的id是数字类型，需要转换为Long
        Long githubId = Long.parseLong(githubAttributes.get("id").toString());
        // GitHub用户名（login字段）
        String username = (String) githubAttributes.get("login");
        // 头像URL
        String avatarUrl = (String) githubAttributes.get("avatar_url");
        // 个人主页URL
        String profileUrl = (String) githubAttributes.get("html_url");
        // 电子邮件（可能为null，取决于用户隐私设置）
        String email = (String) githubAttributes.get("email");

        // 检查该GitHub用户是否已在本地系统中存在
        return userRepository.findByGithubId(githubId)
                .map(existingUser -> {
                    // 如果用户已存在，更新用户信息
                    existingUser.setUsername(username);
                    existingUser.setAvatarUrl(avatarUrl);
                    existingUser.setProfileUrl(profileUrl);
                    existingUser.setEmail(email);
                    existingUser.setAccessToken(accessToken); // 更新访问令牌
                    existingUser.setUpdatedAt(LocalDateTime.now()); // 更新时间戳
                    return existingUser;
                })
                .orElseGet(() -> {
                    // 如果用户不存在，创建新用户
                    User newUser = new User();
                    newUser.setGithubId(githubId);
                    newUser.setUsername(username);
                    newUser.setAvatarUrl(avatarUrl);
                    newUser.setProfileUrl(profileUrl);
                    newUser.setEmail(email);
                    newUser.setAccessToken(accessToken);
                    newUser.setCreatedAt(LocalDateTime.now()); // 创建时间戳
                    newUser.setUpdatedAt(LocalDateTime.now()); // 更新时间戳
                    return newUser;
                });
    }
}