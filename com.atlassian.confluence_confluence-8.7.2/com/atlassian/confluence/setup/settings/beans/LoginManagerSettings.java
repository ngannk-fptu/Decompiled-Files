/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings.beans;

import com.atlassian.confluence.user.AuthenticatorOverwrite;
import java.io.Serializable;

public class LoginManagerSettings
implements Serializable {
    private static final long serialVersionUID = 298216386342761156L;
    private boolean enableElevatedSecurityCheck = true;
    private int loginAttemptsThreshold = 3;

    public LoginManagerSettings() {
    }

    public LoginManagerSettings(LoginManagerSettings settings) {
        this.enableElevatedSecurityCheck = settings.isEnableElevatedSecurityCheck();
        this.loginAttemptsThreshold = settings.getLoginAttemptsThreshold();
    }

    public boolean isEnableElevatedSecurityCheck() {
        return this.enableElevatedSecurityCheck && !AuthenticatorOverwrite.isPasswordConfirmationDisabled();
    }

    public void setEnableElevatedSecurityCheck(boolean enableElevatedSecurityCheck) {
        this.enableElevatedSecurityCheck = enableElevatedSecurityCheck;
    }

    public int getLoginAttemptsThreshold() {
        return this.loginAttemptsThreshold;
    }

    public void setLoginAttemptsThreshold(int loginAttemptsThreshold) {
        this.loginAttemptsThreshold = loginAttemptsThreshold;
    }
}

