package com.nutech.simsppob.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BaseResponse<T> {
    private int status;
    private String message;
    private T data;

    public BaseResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }
}
