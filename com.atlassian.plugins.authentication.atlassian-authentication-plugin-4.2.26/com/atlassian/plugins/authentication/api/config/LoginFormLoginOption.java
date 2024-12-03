/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.api.config;

import com.atlassian.plugins.authentication.api.config.LoginOption;

public class LoginFormLoginOption
extends LoginOption {
    public static final LoginFormLoginOption INSTANCE = new LoginFormLoginOption();

    private LoginFormLoginOption() {
        super(LoginOption.Type.LOGIN_FORM);
    }
}

