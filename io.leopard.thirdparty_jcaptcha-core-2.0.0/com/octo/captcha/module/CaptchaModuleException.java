/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.module;

public class CaptchaModuleException
extends RuntimeException {
    private Throwable cause;

    public CaptchaModuleException() {
    }

    public CaptchaModuleException(String message) {
        super(message);
    }

    public CaptchaModuleException(Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }

    public CaptchaModuleException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

