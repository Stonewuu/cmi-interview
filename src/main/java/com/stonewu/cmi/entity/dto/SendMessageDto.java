package com.stonewu.cmi.entity.dto;

import lombok.Data;

@Data
public class SendMessageDto {

    private String acceptorTel;

    private Integer qos;

    private MessageDto templateParam;

    private String timestamp;
}
