package com.cy.base;

import lombok.Data;

/**
 * API格式封装
 * @author: 𝓛.𝓕.𝓠
 */
@Data
public class ApiResponse {
    private int code;
    private String msg;
    private Object data;
    private int count;

    public ApiResponse() {
    }

    public ApiResponse(int code, String msg, int count, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.count = count;
    }

    public static ApiResponse of(int code, String msg, int count, Object data) {
        return new ApiResponse(code, msg, count, data);
    }


}

