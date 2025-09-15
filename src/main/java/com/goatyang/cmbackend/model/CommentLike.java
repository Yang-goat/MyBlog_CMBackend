package com.goatyang.cmbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Data
@Entity
@Table(
        name = "comment_like",
        // 联合唯一约束：同一用户对同一评论只能点赞一次
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "comment_id"})
)
@DynamicInsert
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // 点赞用户ID（关联user表）

    // 关联点赞用户信息（可选，用于查询谁点赞了某评论）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "comment_id", nullable = false)
    private Long commentId; // 被点赞评论ID（关联comment表）

    // 关联被点赞的评论（可选，用于查询某评论的所有点赞）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    private Comment comment;

    @Column(name = "like_time", nullable = false)
    private LocalDateTime likeTime; // 点赞时间

    @PrePersist
    protected void onCreate() {
        likeTime = LocalDateTime.now(); // 自动记录点赞时间
    }
}
