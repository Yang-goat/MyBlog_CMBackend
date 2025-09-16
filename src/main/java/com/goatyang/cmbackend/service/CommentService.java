package com.goatyang.cmbackend.service;

import com.goatyang.cmbackend.model.Comment;
import com.goatyang.cmbackend.model.User;
import com.goatyang.cmbackend.repository.CommentRepository;
import com.goatyang.cmbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    /** 创建评论 */
    public Comment createComment(Long userId, String articlePath, String content) {
        User user = userRepository.findByGithubId(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setArticlePath(articlePath);
        comment.setContent(content);
        comment.setLikeCount(0L);

        return commentRepository.save(comment);
    }

    /** 获取所有评论 */
    public List<Comment> getAllComments() {
        return commentRepository.findAllWithUser();
    }

    /** 根据用户ID获取评论 */
    public List<Comment> getCommentsByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    /** 根据用户名获取评论 */
    public List<Comment> getCommentsByUsername(String username){
        Optional<User> user = userRepository.findByUsername(username);
        Long userId = user.map(User::getId).orElse(null);
        return commentRepository.findByUserId(userId);
    }

    /** 根据文章路径获取评论 */
    public List<Comment> getCommentsByArticlePath(String articlePath) {
        return commentRepository.findByArticlePath(articlePath);
    }

    /** 根据时间范围获取评论 */
    public List<Comment> getCommentsByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return commentRepository.findByCreatedAtBetween(startTime, endTime);
    }

    /** 根据评论id删除评论 */
    @Transactional
    public void deleteCommentByCommentId(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("评论不存在");
        }
        commentRepository.deleteById(commentId);
    }

    /** 根据用户ID删除该用户的所有评论 */
    @Transactional
    public void deleteAllCommentsByUserId(Long userId) {
        // 先检查用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 删除该用户的所有评论
        commentRepository.deleteByUserId(userId);
    }
}
