package com.stonewu.cmi.controller;

import com.stonewu.cmi.entity.enums.ApiResultType;
import com.stonewu.cmi.entity.result.CommonResult;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
    public CommonResult handleParameterVerificationException(Exception e) {
        return new CommonResult(ApiResultType.PARAM_ERROR);
    }

}
