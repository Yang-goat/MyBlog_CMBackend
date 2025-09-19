package com.goatyang.cmbackend.service;

import com.goatyang.cmbackend.model.Comment;
import com.goatyang.cmbackend.model.CommentLike;
import com.goatyang.cmbackend.model.User;
import com.goatyang.cmbackend.repository.CommentLikeRepository;
import com.goatyang.cmbackend.repository.CommentRepository;
import com.goatyang.cmbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentLikeService(CommentLikeRepository commentLikeRepository,
                              CommentRepository commentRepository,
                              UserRepository userRepository) {
        this.commentLikeRepository = commentLikeRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    /** 点赞评论 */
    @Transactional
    public Map<String, Object> likeComment(Long userId, Long commentId) {
        User user = userRepository.findByGithubId(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));

        CommentLike like = new CommentLike();
        like.setUser(user);
        like.setComment(comment);
        like.setIsCanceled(false);
        // 评论总点赞数+1
        commentRepository.incrementLikeCount(commentId);
        commentLikeRepository.save(like);

        return buildLikeInfo(userId, commentId);
    }

    /** 取消点赞 */
//    @Transactional
//    public Map<String, Object> unlikeComment(Long userId, Long commentId) {
//        CommentLike like = commentLikeRepository.findByCommentIdAndUserIdWithFetch(commentId, userId)
//                .orElseThrow(() -> new IllegalArgumentException("点赞记录不存在"));
//
//        if (like.getIsCanceled()) {
//            throw new IllegalArgumentException("该点赞已取消");
//        }
//
//        like.setIsCanceled(true);
//        // 评论总数-1
//        commentRepository.decrementLikeCount(commentId);
//        commentLikeRepository.save(like);
//
//        return buildLikeInfo(userId, commentId);
//    }

    /** 获取某评论的所有点赞记录 */
    public List<CommentLike> getLikesByCommentId(Long commentId) {
        return commentLikeRepository.findValidLikesByCommentIdWithUser(commentId);
    }

    /** 获取某用户的所有点赞记录 */
    public List<CommentLike> getLikesByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Long userId = user.map(User::getId).orElse(null);
        return commentLikeRepository.findByUserIdWithCommentAndUsers(userId);
    }

    /** 删除某用户的所有点赞记录 */
    @Transactional
    public Map<String, Object> deleteAllLikesByUserId(Long userId) {
        List<CommentLike> likes = commentLikeRepository.findByUserIdWithCommentAndUsers(userId);

        int deletedCount = 0;
        if (!likes.isEmpty()) {
            // 对于每条有效的点赞记录，减少对应评论的点赞数
            for (CommentLike like : likes) {
//                if (!like.getIsCanceled()) {
                    commentRepository.decrementLikeCount(like.getComment().getCommentId());
//                }
            }
            commentLikeRepository.deleteAll(likes);
            deletedCount = likes.size();
        }

        // 统一返回格式
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("deletedCount", deletedCount);
        result.put("message", deletedCount > 0 ? "删除成功" : "没有找到点赞记录");
        return result;
    }


    /** 删除某评论的所有点赞记录 */
    @Transactional
    public Map<String, Object> deleteAllLikesByCommentId(Long commentId) {
        List<CommentLike> likes = commentLikeRepository.findValidLikesByCommentIdWithUser(commentId);

        int deletedCount = 0;
        if (!likes.isEmpty()) {
            // 计算有效的点赞数量并重置评论的点赞数为0
            long validLikeCount = likes.stream().filter(like -> !like.getIsCanceled()).count();
            if (validLikeCount > 0) {
                // 直接将点赞数设置为0，比多次减1更高效
                commentRepository.resetLikeCount(commentId);
            }

            commentLikeRepository.deleteAll(likes);
            deletedCount = likes.size();
        }

        // 统一返回格式
        Map<String, Object> result = new HashMap<>();
        result.put("commentId", commentId);
        result.put("deletedCount", deletedCount);
        result.put("message", deletedCount > 0 ? "删除成功" : "该评论没有点赞记录");
        return result;
    }


    /** 封装点赞信息 */
    private Map<String, Object> buildLikeInfo(Long userId, Long commentId) {
        // 从评论实体中获取点赞数，而不是通过计数查询，更高效
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));

        boolean liked = commentLikeRepository.findByCommentIdAndUserIdWithFetch(commentId, userId)
                .map(l -> !l.getIsCanceled())
                .orElse(false);

        Map<String, Object> result = new HashMap<>();
        result.put("commentId", commentId);
        result.put("likeCount", comment.getLikeCount());
        result.put("liked", liked);
        return result;
    }
}
