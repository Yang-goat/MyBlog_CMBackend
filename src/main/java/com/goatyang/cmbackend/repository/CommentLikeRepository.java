package com.goatyang.cmbackend.repository;

import com.goatyang.cmbackend.model.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    /**
     * 检查用户是否已给某评论点赞（带评论和用户信息，避免懒加载）
     */
    @Query("SELECT cl FROM CommentLike cl " +
            "JOIN FETCH cl.comment " +
            "JOIN FETCH cl.user " +
            "WHERE cl.comment.commentId = :commentId AND cl.user.id = :userId")
    Optional<CommentLike> findByCommentIdAndUserIdWithFetch(@Param("commentId") Long commentId,
                                                            @Param("userId") Long userId);

    /**
     * 统计某评论的有效点赞数（未取消的点赞）
     */
    @Query("SELECT COUNT(cl) FROM CommentLike cl " +
            "WHERE cl.comment.commentId = :commentId AND cl.isCanceled = :isCanceled")
    long countByCommentIdAndIsCanceled(@Param("commentId") Long commentId,
                                       @Param("isCanceled") Boolean isCanceled);

    /**
     * 查询某评论的所有有效点赞记录（带用户信息，过滤掉已取消的点赞）
     */
    @Query("SELECT cl FROM CommentLike cl " +
            "JOIN FETCH cl.user " +
            "JOIN FETCH cl.comment c " +
            "JOIN FETCH c.user " +
            "WHERE cl.comment.commentId = :commentId AND cl.isCanceled = false")
    List<CommentLike> findValidLikesByCommentIdWithUser(@Param("commentId") Long commentId);

    /**
     * 查询用户点赞的所有评论记录（带评论信息和用户信息）
     */
    @Query("SELECT cl FROM CommentLike cl " +
            "JOIN FETCH cl.comment c " +
            "JOIN FETCH c.user " +
            "JOIN FETCH cl.user " +
            "WHERE cl.user.id = :userId")
    List<CommentLike> findByUserIdWithCommentAndUsers(@Param("userId") Long userId);

    /**
     * 批量删除某评论的所有点赞记录
     */
    void deleteByComment_CommentId(Long commentId);

    /**
     * 批量删除某用户的所有点赞记录
     */
    void deleteByUser_Id(Long userId);
}
