package com.goatyang.cmbackend.controller;


import com.goatyang.cmbackend.model.Comment;
import com.goatyang.cmbackend.model.User;
import com.goatyang.cmbackend.service.CommentLikeService;
import com.goatyang.cmbackend.service.CommentService;
import com.goatyang.cmbackend.service.UserService;
import com.goatyang.cmbackend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/users")
public class AdminUserController {

    private final UserService userService;
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

    @Autowired
    public AdminUserController(UserService userService, CommentService commentService, CommentLikeService commentLikeService) {
        this.userService = userService;
        this.commentService = commentService;
        this.commentLikeService = commentLikeService;
    }

    /** 获取所有用户 */
    @GetMapping
    public ApiResponse<List<User>> getAllUsers() {
        System.out.println("=====================管理系统开始获取所有用户");
        return ApiResponse.success(userService.getAllUsers());
    }

    /** 根据 GitHub ID 获取用户 */
    @GetMapping("/github/{githubId}")
    public ApiResponse<User> getUserByGithubId(@PathVariable Long githubId) {
        return userService.getUserByGithubId(githubId)
                .map(ApiResponse::success)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    /** 根据邮箱获取用户 */
    @GetMapping("/email/{email}")
    public ApiResponse<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ApiResponse::success)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    /** 根据用户名获取用户 */
    @GetMapping("/username/{username}")
    public ApiResponse<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ApiResponse::success)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    /** 更新用户评论权限 */
    @PutMapping("/{userId}")
    public ApiResponse<User> updateUser(@PathVariable Long userId, @RequestBody User userDetails) {
        System.out.println("=========================更新用户评论权限："+userId);
        System.out.println(userDetails);
        return ApiResponse.success(userService.updateUser(userId, userDetails));
    }

    /** 删除用户（级联删除评论及点赞） */
    @DeleteMapping("/{userId}")
    public ApiResponse<Map<String, Object>> deleteUser(@PathVariable Long userId) {
        // 记录返回结果
        Map<String, Object> result = new HashMap<>();

        // 删除用户的点赞
        Map<String, Object> likesDeleted = commentLikeService.deleteAllLikesByUserId(userId);
        result.put("likesDeleted", likesDeleted);

        // 删除用户的评论以及相关点赞
        List<Comment> comments = commentService.getCommentsByUserId(userId);
        List<Map<String, Object>> commentLikesDeleted = new ArrayList<>();
        for (Comment comment : comments) {
            Map<String, Object> commentLikeDeleteResult =
                    commentLikeService.deleteAllLikesByCommentId(comment.getCommentId());
            commentLikesDeleted.add(commentLikeDeleteResult);
        }
        result.put("commentLikesDeleted", commentLikesDeleted);

        // 删除用户的评论
        commentService.deleteAllCommentsByUserId(userId);
        result.put("commentsDeleted", comments.size());

        // 删除用户本身
        userService.deleteUser(userId);
        result.put("userDeleted", userId);

        return ApiResponse.success(result);
    }
}
