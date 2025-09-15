package com.goatyang.cmbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comment")
@DynamicInsert
@DynamicUpdate
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id; // 评论ID（前端评论列表展示）

    @Column(name = "user_id", nullable = false)
    private Long userId; // 关联用户ID（对应user表的id）

    // 关联用户信息（用于查询评论时同步获取用户名等信息）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "article_path", nullable = false, length = 255)
    private String articlePath; // 文章路径（用于"根据article_path查找评论"）

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content; // 评论内容（前端评论列表展示）

    @Column(name = "like_count", nullable = false)
    private Integer likeCount; // 点赞数（前端点赞统计页核心字段）

    @Column(name = "comment_time", nullable = false)
    private LocalDateTime commentTime; // 评论时间（前端列表排序、筛选）

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted; // 逻辑删除标志（0=未删除，1=已删除）

    @PrePersist
    protected void onCreate() {
        commentTime = LocalDateTime.now();
        if (likeCount == null) likeCount = 0; // 默认点赞数为0
        if (isDeleted == null) isDeleted = false; // 默认未删除
    }
}
