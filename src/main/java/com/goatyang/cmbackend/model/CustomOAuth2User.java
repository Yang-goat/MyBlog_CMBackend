package com.goatyang.cmbackend.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * 自定义OAuth2用户模型
 * 扩展了Spring Security的OAuth2User，添加了本地系统的用户ID，
 * 用于关联第三方OAuth2认证信息与本地数据库中的用户记录
 */
public class CustomOAuth2User implements OAuth2User {

    // 本地数据库中的用户ID，使用Lombok的@Getter自动生成getter方法
    // 用于在认证后关联第三方账号与本地系统用户
    @Getter
    private final Long userId;

    // 封装的原始OAuth2User对象，用于 delegate 大部分接口方法的实现
    private final OAuth2User oAuth2User;

    /**
     * 构造方法
     *
     * @param userId 本地数据库中的用户ID
     * @param attributes 从OAuth2提供商获取的用户属性集合
     * @param nameAttributeKey 用于获取用户名的属性键（如"login"或"username"）
     */
    public CustomOAuth2User(Long userId, Map<String, Object> attributes, String nameAttributeKey) {
        this.userId = userId;
        // 创建默认的OAuth2User实现，传入属性和名称属性键
        // 第一个参数为权限集合，这里暂时为null
        this.oAuth2User = new DefaultOAuth2User(null, attributes, nameAttributeKey);
    }

    /**
     * 获取从OAuth2提供商返回的用户属性
     * 如GitHub的用户信息可能包含login、avatar_url、id等
     */
    @Override
    public Map<String, Object> getAttributes() {
        // 委托给内部的oAuth2User对象实现
        return oAuth2User.getAttributes();
    }

    /**
     * 获取用户的权限集合
     * 在本实现中未自定义权限，直接使用原始OAuth2User的权限
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 委托给内部的oAuth2User对象实现
        return oAuth2User.getAuthorities();
    }

    /**
     * 获取用户名，通常是OAuth2提供商返回的唯一标识
     * 具体值由构造方法中的nameAttributeKey指定
     */
    @Override
    public String getName() {
        // 委托给内部的oAuth2User对象实现
        return oAuth2User.getName();
    }

}