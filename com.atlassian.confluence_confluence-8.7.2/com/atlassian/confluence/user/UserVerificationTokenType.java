/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.user;

public enum UserVerificationTokenType {
    PASSWORD_RESET("password-reset-tokens"),
    USER_SIGNUP("user-signup-confirmation-tokens");

    private String contextName;

    private UserVerificationTokenType(String contextName) {
        this.contextName = contextName;
    }

    public String getContextName() {
        return this.contextName;
    }
}

