package com.goatyang.cmbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user")
@DynamicInsert
@DynamicUpdate
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "github_id", unique = true) // 非必填，兼容GitHub登录场景
    private Long githubId;

    @Column(name = "username", nullable = false, length = 100)
    private String username; // 前端用户列表、评论列表展示用

    @Column(name = "password", nullable = false)
    private String password; // 新增用户时的密码存储（需加密）

    @Column(name = "email", length = 150)
    private String email; // 前端用户查询、列表展示用

    @Column(name = "status", nullable = false)
    private Integer status; // 账号状态：1-正常，0-禁用（前端用户状态控制）

    @Column(name = "comment_permission", nullable = false)
    private Boolean commentPermission; // 评论权限开关（前端权限控制页）

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 注册时间（前端用户列表展示）

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = 1; // 默认正常
        if (commentPermission == null) commentPermission = true; // 默认允许评论
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
