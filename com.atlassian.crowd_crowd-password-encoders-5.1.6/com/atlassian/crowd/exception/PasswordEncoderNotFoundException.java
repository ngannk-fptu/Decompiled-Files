/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

public class PasswordEncoderNotFoundException
extends RuntimeException {
    public PasswordEncoderNotFoundException(String msg) {
        super(msg);
    }

    public PasswordEncoderNotFoundException(String msg, Throwable ex) {
        super(msg, ex);
    }
}

