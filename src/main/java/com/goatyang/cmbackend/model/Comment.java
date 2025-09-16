package com.goatyang.cmbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Entity
@Table(name = "comment")
@DynamicInsert
@DynamicUpdate
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false, updatable = false)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY) // 多个评论对应一个用户
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "article_path", nullable = false, length = 255)
    private String articlePath; // 对应文章路径

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content; // 评论内容

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
