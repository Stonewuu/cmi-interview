package com.stonewu.cmi.entity.enums;

import java.io.Serializable;

public enum ApiResultType implements Serializable {
    /**
     * 成功
     */
    SUCCESS(200, "success"),
    /**
     * 未获取到授权
     */
    PARAM_ERROR(400, "请求参数错误"),
    /**
     * 未获取到授权
     */
    NO_AUTH_ERROR(403, "禁止未经授权访问"),
    /**
     * 未知错误
     */
    SERVER_ERROR(500, "内部服务器错误");

    private final int code;
    private final String msg;

    private ApiResultType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
