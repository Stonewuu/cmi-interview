package com.stonewu.cmi.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserPasswordDto {
    @NotBlank
    @Size(min = 3, max = 32)
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "请求参数错误")
    private String userName;
    @NotBlank
    @Size(min = 8, max = 64)
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "请求参数错误")
    private String password;
}
