package com.stonewu.cmi.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class MessageDto {
    @NotBlank
    @Size(min = 1, max = 64)
    private String title;

    private String content;
}
