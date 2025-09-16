package com.goatyang.cmbackend.repository;

import com.goatyang.cmbackend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 查询所有评论并加载用户信息
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.user ORDER BY c.createdAt DESC")
    List<Comment> findAllWithUser();

    /**
     * 根据文章路径查询评论（带用户信息）
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.user " +
            "WHERE c.articlePath = :articlePath ORDER BY c.createdAt DESC")
    List<Comment> findByArticlePath(@Param("articlePath") String articlePath);

    /**
     * 根据用户ID查询该用户发布的所有评论（带用户信息）
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.user " +
            "WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<Comment> findByUserId(@Param("userId") Long userId);

    /**
     * 查询某个时间范围内的评论（带用户信息）
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.user " +
            "WHERE c.createdAt BETWEEN :startTime AND :endTime ORDER BY c.createdAt DESC")
    List<Comment> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 统计某篇文章的评论总数
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.articlePath = :articlePath")
    long countByArticlePath(@Param("articlePath") String articlePath);

    /**
     * 根据评论ID查询评论详情（带用户信息，避免懒加载）
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.commentId = :commentId")
    Optional<Comment> findByCommentIdWithUser(@Param("commentId") Long commentId);

    /**
     * 批量删除某用户的所有评论
     */
    @Transactional
    void deleteByUserId(Long userId);

    /**
     * 批量删除某篇文章的所有评论
     */
    @Transactional
    void deleteByArticlePath(String articlePath);

    /**
     * 为指定评论的点赞数加1
     */
    @Modifying
    @Transactional
    @Query("UPDATE Comment c SET c.likeCount = c.likeCount + 1 WHERE c.commentId = :commentId")
    void incrementLikeCount(@Param("commentId") Long commentId);

    /**
     * 为指定评论的点赞数减1（确保点赞数不会小于0）
     */
    @Modifying
    @Transactional
    @Query("UPDATE Comment c SET c.likeCount = GREATEST(c.likeCount - 1, 0) WHERE c.commentId = :commentId")
    void decrementLikeCount(@Param("commentId") Long commentId);

    /**
     * 重置指定评论的点赞数为0
     */
    @Modifying
    @Transactional
    @Query("UPDATE Comment c SET c.likeCount = 0 WHERE c.commentId = :commentId")
    void resetLikeCount(@Param("commentId") Long commentId);
}
