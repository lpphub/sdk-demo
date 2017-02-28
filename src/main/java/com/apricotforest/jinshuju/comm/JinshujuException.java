package com.apricotforest.jinshuju.comm;

public class JinshujuException extends Exception {
    private int code = -1;
    private String msg;

    public JinshujuException(Exception e) {
        super(e);
        this.msg = e.getMessage();
    }

    public JinshujuException(String msg) {
        this(-1, msg);
    }

    public JinshujuException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public JinshujuException(String msg, Exception e) {
        super(e);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }
}
