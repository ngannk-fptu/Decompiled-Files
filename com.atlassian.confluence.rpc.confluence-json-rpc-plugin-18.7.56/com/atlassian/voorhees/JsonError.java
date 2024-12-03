/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.voorhees;

import com.atlassian.voorhees.ErrorCode;
import java.io.PrintWriter;
import java.io.StringWriter;

public class JsonError {
    private final int code;
    private final String message;
    private final Object data;

    public JsonError(ErrorCode code, String message) {
        this(code.intValue(), message);
    }

    public JsonError(ErrorCode code, String message, Object data) {
        this(code.intValue(), message, data);
    }

    public JsonError(int code, String message) {
        this(code, message, null);
    }

    public JsonError(int numericCode, String message, Object data) {
        this.code = numericCode;
        this.message = message;
        this.data = data instanceof Exception ? this.convertExceptionToString((Exception)data) : data;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public Object getData() {
        return this.data;
    }

    private String convertExceptionToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        out.write(e.toString());
        out.write("\n");
        e.printStackTrace(out);
        out.flush();
        return sw.toString();
    }
}

