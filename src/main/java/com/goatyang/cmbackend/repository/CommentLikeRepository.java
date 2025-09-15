package com.goatyang.cmbackend.repository;

import com.goatyang.cmbackend.model.CommentLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 评论点赞数据访问层接口
 * 用于与数据库交互，处理评论点赞相关的CRUD操作
 */
@Repository
// 继承JpaRepository<CommentLike, Long>，泛型为实体类CommentLike和主键类型Long
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    /**
     * 检查用户是否已给某评论点赞（用于避免重复点赞）
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 点赞记录（存在则表示已点赞）
     */
    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);

    /**
     * 统计某评论的有效点赞数（未取消的点赞）
     * @param commentId 评论ID
     * @param isCanceled 点赞状态（0=有效，1=已取消）
     * @return 有效点赞总数
     */
    long countByCommentIdAndIsCanceled(Long commentId, Integer isCanceled);

    /**
     * 查询用户点赞的所有评论（用于“用户个人中心-我的点赞”）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页的点赞记录列表（包含评论ID等信息）
     */
    Page<CommentLike> findByUserId(Long userId, Pageable pageable);

    /**
     * 批量删除某评论的所有点赞记录（用于“删除评论时级联删除其点赞”）
     * @param commentId 评论ID
     */
    void deleteByCommentId(Long commentId);

    /**
     * 批量删除某用户的所有点赞记录（用于“删除用户时级联删除其点赞”）
     * @param userId 用户ID
     */
    void deleteByUserId(Long userId);
}
