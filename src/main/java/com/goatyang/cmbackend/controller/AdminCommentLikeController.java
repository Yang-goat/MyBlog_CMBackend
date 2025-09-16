package com.goatyang.cmbackend.controller;

import com.goatyang.cmbackend.model.CommentLike;
import com.goatyang.cmbackend.service.CommentLikeService;
import com.goatyang.cmbackend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/comment-likes")
public class AdminCommentLikeController {

    private final CommentLikeService commentLikeService;

    @Autowired
    public AdminCommentLikeController(CommentLikeService commentLikeService) {
        this.commentLikeService = commentLikeService;
    }

    /** 获取某评论的所有点赞记录 */
    @GetMapping("/comment/{commentId}/all")
    public ApiResponse<List<CommentLike>> getLikesByCommentId(@PathVariable Long commentId) {
        return ApiResponse.success(commentLikeService.getLikesByCommentId(commentId));
    }

    /** 获取某用户的所有点赞记录 */
    @GetMapping("/username/{username}")
    public ApiResponse<List<CommentLike>> getLikesByUserId(@PathVariable String username) {
        return ApiResponse.success(commentLikeService.getLikesByUsername(username));
    }
}
