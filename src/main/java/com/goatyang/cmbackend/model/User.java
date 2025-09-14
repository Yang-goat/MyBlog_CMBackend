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

    @Column(name = "github_id", nullable = false, unique = true)
    private Long githubId;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "profile_url", length = 255)
    private String profileUrl;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "access_token", length = 255)
    private String accessToken;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
