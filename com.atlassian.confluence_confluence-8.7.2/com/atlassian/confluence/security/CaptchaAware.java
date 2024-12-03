/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security;

public interface CaptchaAware {
    default public boolean mustValidateCaptcha() {
        return false;
    }
}

