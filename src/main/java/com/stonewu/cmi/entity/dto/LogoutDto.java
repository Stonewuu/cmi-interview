package com.stonewu.cmi.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class LogoutDto {
    @NotBlank
    @Size(min = 3, max = 32)
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "请求参数错误")
    private String userName;
    @NotBlank
    @Size(max = 50, message = "请求参数错误")
    private String sessionId;
}
