/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.service;

public class CaptchaServiceException
extends RuntimeException {
    private Throwable cause;

    public CaptchaServiceException(String message) {
        super(message);
    }

    public CaptchaServiceException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public CaptchaServiceException(Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

