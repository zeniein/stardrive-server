package cn.zeniein.stardrive.handler;

import cn.hutool.crypto.CryptoException;
import cn.zeniein.stardrive.common.ResponseData;
import cn.zeniein.stardrive.common.ResponseEnum;
import cn.zeniein.stardrive.exception.BizException;
import cn.zeniein.stardrive.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Map<String, Object> notFoundExceptionHandler(NotFoundException e) {
        Map<String, Object> error = new HashMap<>(16);
        error.put("errorMsg", e.getErrorMsg());
        error.put("errorCode", e.getErrorCode());
        return error;
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseData<?> exceptionHandler(Exception e) {
        if (e instanceof BizException) {
            log.error("biz exec error, e = ", e);
            return ResponseData.create((BizException) e);
        }

        if (e instanceof BindException) {
            log.info("request parameter error, e = ", e);
            return ResponseData.create(ResponseEnum.ERROR);
        }

        if (e instanceof CryptoException) {
            log.info("CryptoException error, e = {}", e.getMessage());
            return ResponseData.create(ResponseEnum.ERROR);
        }

        log.error("unexpected error, e = ", e);
        return ResponseData.create(ResponseEnum.UNEXPECTED);
    }

}
