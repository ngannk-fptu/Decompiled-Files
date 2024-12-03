/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.css.sac;

public class CSSException
extends RuntimeException {
    protected String s;
    public static final short SAC_UNSPECIFIED_ERR = 0;
    public static final short SAC_NOT_SUPPORTED_ERR = 1;
    public static final short SAC_SYNTAX_ERR = 2;
    protected static final String S_SAC_UNSPECIFIED_ERR = "unknown error";
    protected static final String S_SAC_NOT_SUPPORTED_ERR = "not supported";
    protected static final String S_SAC_SYNTAX_ERR = "syntax error";
    protected Exception e;
    protected short code;

    public CSSException() {
    }

    public CSSException(String string) {
        this.code = 0;
        this.s = string;
    }

    public CSSException(Exception exception) {
        this.code = 0;
        this.e = exception;
    }

    public CSSException(short s) {
        this.code = s;
    }

    public CSSException(short s, String string, Exception exception) {
        this.code = s;
        this.s = string;
        this.e = exception;
    }

    public String getMessage() {
        if (this.s != null) {
            return this.s;
        }
        if (this.e != null) {
            return this.e.getMessage();
        }
        switch (this.code) {
            case 0: {
                return S_SAC_UNSPECIFIED_ERR;
            }
            case 1: {
                return S_SAC_NOT_SUPPORTED_ERR;
            }
            case 2: {
                return S_SAC_SYNTAX_ERR;
            }
        }
        return null;
    }

    public short getCode() {
        return this.code;
    }

    public Exception getException() {
        return this.e;
    }
}

