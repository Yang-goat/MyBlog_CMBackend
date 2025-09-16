package com.goatyang.cmbackend.controller;

import com.goatyang.cmbackend.model.CommentLike;
import com.goatyang.cmbackend.service.CommentLikeService;
import com.goatyang.cmbackend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment-likes")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @Autowired
    public CommentLikeController(CommentLikeService commentLikeService) {
        this.commentLikeService = commentLikeService;
    }

    /** 点赞评论 */
    @PostMapping
    public ApiResponse<Map<String, Object>> likeComment(@RequestParam Long userId,
                                                        @RequestParam Long commentId) {
        System.out.println("==================开始创建点赞："+userId+"<UNK>"+commentId);
        return ApiResponse.success(commentLikeService.likeComment(userId, commentId));
    }

    /** 取消点赞 */
    @PatchMapping("/cancel")
    public ApiResponse<Map<String, Object>> unlikeComment(@RequestParam Long userId,
                                                          @RequestParam Long commentId) {
        return ApiResponse.success(commentLikeService.unlikeComment(userId, commentId));
    }
}
