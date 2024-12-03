/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

public class HttpException
extends IOException {
    private String reason;
    private int reasonCode = 200;
    private final Throwable cause;

    public HttpException() {
        this.cause = null;
    }

    public HttpException(String message) {
        super(message);
        this.cause = null;
    }

    public HttpException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
        try {
            Class[] paramsClasses = new Class[]{Throwable.class};
            Method initCause = Throwable.class.getMethod("initCause", paramsClasses);
            initCause.invoke((Object)this, cause);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }

    @Override
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }

    @Override
    public void printStackTrace(PrintStream s) {
        block2: {
            try {
                Class[] paramsClasses = new Class[]{};
                this.getClass().getMethod("getStackTrace", paramsClasses);
                super.printStackTrace(s);
            }
            catch (Exception ex) {
                super.printStackTrace(s);
                if (this.cause == null) break block2;
                s.print("Caused by: ");
                this.cause.printStackTrace(s);
            }
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        block2: {
            try {
                Class[] paramsClasses = new Class[]{};
                this.getClass().getMethod("getStackTrace", paramsClasses);
                super.printStackTrace(s);
            }
            catch (Exception ex) {
                super.printStackTrace(s);
                if (this.cause == null) break block2;
                s.print("Caused by: ");
                this.cause.printStackTrace(s);
            }
        }
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReasonCode(int code) {
        this.reasonCode = code;
    }

    public int getReasonCode() {
        return this.reasonCode;
    }
}

