package com.stonewu.cmi.entity.result;


import com.stonewu.cmi.entity.enums.ApiResultType;
import lombok.Data;

@Data
public class LoginResult extends CommonResult {

    private static final long serialVersionUID = 2602486984901223568L;

    private String sessionId;

    public LoginResult() {

    }

    public LoginResult(ApiResultType result) {
        super(result);
    }

    public LoginResult(ApiResultType result, String data) {
        super(result);
        this.sessionId = data;
    }

    public LoginResult(Integer code, String msg, String data) {
        super(code, msg);
        this.sessionId = data;
    }

    public LoginResult(Integer code, String msg) {
        super(code, msg);
    }

}
