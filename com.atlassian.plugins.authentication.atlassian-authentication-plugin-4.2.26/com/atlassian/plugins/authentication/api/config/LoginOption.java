/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.api.config;

import java.util.Objects;

public abstract class LoginOption {
    private final Type type;

    public LoginOption(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LoginOption that = (LoginOption)o;
        return this.type == that.type;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type});
    }

    public static enum Type {
        LOGIN_FORM,
        IDP;

    }
}

