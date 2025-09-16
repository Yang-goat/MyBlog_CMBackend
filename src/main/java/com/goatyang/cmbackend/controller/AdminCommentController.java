package com.goatyang.cmbackend.controller;


import com.goatyang.cmbackend.model.Comment;
import com.goatyang.cmbackend.model.User;
import com.goatyang.cmbackend.service.CommentService;
import com.goatyang.cmbackend.service.UserService;
import com.goatyang.cmbackend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/api/comments")
public class AdminCommentController {

    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public AdminCommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    /** 获取所有评论 */
    @GetMapping
    public ApiResponse<List<Comment>> getAllComments() {
        return ApiResponse.success(commentService.getAllComments());
    }

    /** 根据用户ID获取评论 */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Comment>> getCommentsByUserId(@PathVariable Long userId) {
        return ApiResponse.success(commentService.getCommentsByUserId(userId));
    }

    /** 根据用户名获取评论 */
    @GetMapping("/username/{username}")
    public ApiResponse<List<Comment>> getCommentsByUserId(@PathVariable String username) {
        return ApiResponse.success(commentService.getCommentsByUsername(username));
    }

    /** 根据文章路径获取评论 */
    @GetMapping("/article/{articlePath}")
    public ApiResponse<List<Comment>> getCommentsByArticlePath(@PathVariable String articlePath) {
        return ApiResponse.success(commentService.getCommentsByArticlePath(articlePath));
    }

    /** 根据时间范围获取评论 */
    @GetMapping("/time")
    public ApiResponse<List<Comment>> getCommentsByCreatedAtBetween(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime
    ) {
        return ApiResponse.success(commentService.getCommentsByCreatedAtBetween(startTime, endTime));
    }

    /** 删除评论 */
    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteCommentByCommentId(commentId);
        return ApiResponse.success(null);
    }
}
