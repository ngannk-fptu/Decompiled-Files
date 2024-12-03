/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha;

public class CaptchaException
extends RuntimeException {
    private Throwable cause;

    public CaptchaException() {
    }

    public CaptchaException(String message) {
        super(message);
    }

    public CaptchaException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public CaptchaException(Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

