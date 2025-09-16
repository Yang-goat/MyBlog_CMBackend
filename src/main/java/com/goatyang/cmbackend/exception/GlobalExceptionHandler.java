package com.goatyang.cmbackend.exception;

import com.goatyang.cmbackend.util.ApiResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return ApiResponse.error(400, message);
    }

    /**
     * 处理 IllegalArgumentException（常见于找不到资源、参数错误）
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        return ApiResponse.error(404, e.getMessage() != null ? e.getMessage() : "资源未找到");
    }

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        // 这里可以打印日志，便于排查问题
        e.printStackTrace();
        return ApiResponse.error(500, "服务器内部错误，请稍后再试");
    }
}
