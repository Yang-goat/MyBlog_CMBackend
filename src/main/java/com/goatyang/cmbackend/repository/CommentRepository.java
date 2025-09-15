package com.goatyang.cmbackend.repository;

import com.goatyang.cmbackend.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 评论数据访问层接口
 * 用于与数据库交互，处理评论相关的CRUD操作
 */
@Repository
// 继承JpaRepository<Comment, Long>，泛型为实体类Comment和主键类型Long
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 根据文章路径查询评论（核心接口：支撑前端“某文章的评论列表”）
     * 支持分页和排序（如按创建时间倒序，最新评论在前）
     * @param articlePath 文章路径（如"/articles/java-basics"）
     * @param pageable 分页参数（页码、每页条数、排序规则）
     * @return 分页的评论列表
     */
    Page<Comment> findByArticlePath(String articlePath, Pageable pageable);

    /**
     * 根据用户ID查询该用户发布的所有评论（用于“用户个人中心-我的评论”）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页的评论列表
     */
    Page<Comment> findByUserId(Long userId, Pageable pageable);

    /**
     * 统计某篇文章的评论总数（用于前端显示“共X条评论”）
     * @param articlePath 文章路径
     * @return 评论总数
     */
    long countByArticlePath(String articlePath);

    /**
     * 根据评论ID查询评论详情（用于“查看单条评论”或“关联点赞信息”）
     * @param commentId 评论ID
     * @return 评论详情（包含内容、用户ID、文章路径等）
     */
    Optional<Comment> findByCommentId(Long commentId);

    /**
     * 批量删除某用户的所有评论（用于“删除用户时级联删除其评论”）
     * @param userId 用户ID
     */
    void deleteByUserId(Long userId);

    /**
     * 批量删除某篇文章的所有评论（用于“删除文章时级联删除其评论”）
     * @param articlePath 文章路径
     */
    void deleteByArticlePath(String articlePath);
}
