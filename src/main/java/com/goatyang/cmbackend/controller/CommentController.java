package com.goatyang.cmbackend.controller;

import com.goatyang.cmbackend.model.Comment;
import com.goatyang.cmbackend.service.CommentService;
import com.goatyang.cmbackend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /** 创建评论 */
    @PostMapping
    public ApiResponse<Comment> createComment(
            @RequestParam Long userId,
            @RequestParam String articlePath,
            @RequestParam String content
    ) {
        System.out.println("=================开始根据文章路径、用户、内容创建评论："+userId+"<UNK>"+articlePath+"<UNK>"+content);
        return ApiResponse.success(commentService.createComment(userId, articlePath, content));
    }

    /** 根据文章路径获取评论 */
    @GetMapping("/article/{articlePath}")
    public ApiResponse<List<Comment>> getCommentsByArticlePath(@PathVariable String articlePath) {
        System.out.println("================开始根据文章路径获取评论:  "+articlePath);

        return ApiResponse.success(commentService.getCommentsByArticlePath(articlePath));
    }
}
