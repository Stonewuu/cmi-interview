package com.stonewu.cmi.entity.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class MessageAuthDto {
    @NotBlank
    @Size(min = 3, max = 32)
    @Pattern(regexp = "^[0-9]*$", message = "请求参数错误")
    private String tels;
    @Range(min = 1, max = 3)
    private Integer  qos;
    @NotBlank
    @Size(min = 3, max = 32)
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "请求参数错误")
    private String userName;
    @NotBlank
    @Size(max = 50, message = "请求参数错误")
    private String sessionId;
}
