package com.nutech.simsppob.util;

public enum ResponseUtilEnum {

    SUCCESS(0),
    BAD_REQUEST(102),
    WRONG_CREDENTIAL(103),
    USER_ALREADY_EXIST(104),
    USER_NOT_EXIST(105),
    INVALID_TOKEN(108);


    public final int statusCode;

    ResponseUtilEnum(int statusCode) {
        this.statusCode = statusCode;
    }
}
