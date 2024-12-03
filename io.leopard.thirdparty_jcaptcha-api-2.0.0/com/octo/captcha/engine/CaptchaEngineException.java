/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.engine;

public class CaptchaEngineException
extends RuntimeException {
    private Throwable cause;

    public CaptchaEngineException() {
    }

    public CaptchaEngineException(String message) {
        super(message);
    }

    public CaptchaEngineException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public CaptchaEngineException(Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

