package com.travelai.exception;

import com.travelai.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 处理自定义业务异常
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Result<?> handleBusinessException(BusinessException e) {
        logger.error("业务异常: {}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    // 处理空指针异常
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public Result<?> handleNullPointerException(NullPointerException e) {
        logger.error("空指针异常: {}", e.getMessage());
        return Result.fail(500, "系统内部错误");
    }

    // 处理非法参数异常
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error("参数异常: {}", e.getMessage());
        return Result.fail(400, e.getMessage());
    }

    // 处理所有其他异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<?> handleException(Exception e) {
        logger.error("系统异常: {}", e.getMessage(), e);
        return Result.fail(500, "系统内部错误");
    }
}
