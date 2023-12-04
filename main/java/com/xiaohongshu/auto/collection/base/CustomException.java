package com.xiaohongshu.auto.collection.base;

public class CustomException extends Exception{
    private String msg;

    /**
     * 101代表ATM通过率异常，102场景数异常，103流量回放异常，104场景书异常
     */
//    private Integer code;

    public CustomException(String msg){
        super(msg);
//        this.code=code;
        this.msg=msg;
    }

    public String getMsg(){
        return this.msg;
    }
}
