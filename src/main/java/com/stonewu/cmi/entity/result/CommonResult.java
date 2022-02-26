package com.stonewu.cmi.entity.result;


import com.stonewu.cmi.entity.enums.ApiResultType;
import lombok.Data;

import java.io.Serializable;

@Data
public class CommonResult implements Serializable {

    private static final long serialVersionUID = 5131829603595933909L;

    private Integer code;

    private String message;

    public CommonResult() {
    }

    public CommonResult(ApiResultType result) {
        this(result.getCode(), result.getMsg());
    }

    public CommonResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
