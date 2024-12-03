/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.security;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

public class LoginDetails
implements Serializable {
    public static final String CAPTCHA_KEY = "captchaId";
    private static final long serialVersionUID = -4337743255077897551L;
    private final LoginSource loginSource;
    private final CaptchaState captchaState;

    public LoginDetails(LoginSource loginSource, @Nullable CaptchaState captchaState) {
        this.loginSource = loginSource;
        this.captchaState = Optional.ofNullable(captchaState).orElse(CaptchaState.NOT_SHOWN);
    }

    public LoginSource getLoginSource() {
        return this.loginSource;
    }

    public CaptchaState getCaptchaState() {
        return this.captchaState;
    }

    public String toString() {
        return "LoginDetails{loginSource=" + this.loginSource + ", captchaState=" + this.captchaState + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LoginDetails that = (LoginDetails)o;
        return this.loginSource == that.loginSource && this.captchaState == that.captchaState;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.loginSource, this.captchaState});
    }

    public static enum CaptchaState {
        PASSED,
        FAILED,
        NOT_SHOWN;

    }

    public static enum LoginSource {
        DIRECT,
        COOKIE,
        SSO,
        UNKNOWN;

    }
}

