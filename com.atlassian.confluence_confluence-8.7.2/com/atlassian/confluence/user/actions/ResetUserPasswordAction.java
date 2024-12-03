/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.security.authentication.InvalidPasswordException
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.security.CaptchaAware;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.user.UserVerificationTokenManager;
import com.atlassian.confluence.user.UserVerificationTokenType;
import com.atlassian.confluence.util.PlainTextToHtmlConverter;
import com.atlassian.user.EntityException;
import com.atlassian.user.security.authentication.InvalidPasswordException;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public class ResetUserPasswordAction
extends ConfluenceActionSupport
implements CaptchaAware {
    private String username;
    private String token;
    private String newPassword;
    private String newPasswordConfirmation;
    private UserVerificationTokenManager userVerificationTokenManager;
    private CaptchaManager captchaManager;

    @Override
    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doDefault() throws Exception {
        return super.doDefault();
    }

    @Override
    public void validate() {
        if (this.getUserByName(this.username) == null) {
            this.addActionError(this.getText("reset.password.error", new String[]{PlainTextToHtmlConverter.encodeHtmlEntities(this.username)}));
        } else if (!this.userVerificationTokenManager.hasValidUserToken(this.username, UserVerificationTokenType.PASSWORD_RESET, this.token) && !this.userVerificationTokenManager.hasValidUserToken(this.username, UserVerificationTokenType.USER_SIGNUP, this.token)) {
            this.addActionError(this.getText("reset.password.error"));
        }
        super.validate();
    }

    public String doResetPassword() {
        if (this.newPassword == null || !this.newPassword.equals(this.newPasswordConfirmation)) {
            this.addFieldError("newPasswordConfirmation", this.getText("your.pass.confirmpass.dont.match"));
            return "input";
        }
        try {
            this.userAccessor.alterPassword(this.getUserByName(this.username), this.newPassword, this.token);
            this.userVerificationTokenManager.clearToken(this.username);
        }
        catch (InvalidPasswordException e) {
            this.addFieldError("newPassword", this.getText("new.pass.invalid"));
            return "input";
        }
        catch (EntityException e) {
            this.addFieldError("newPassword", this.getText("new.pass.error"));
            return "input";
        }
        return "success";
    }

    @Override
    public boolean isPermitted() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setNewPasswordConfirmation(String newPasswordConfirmation) {
        this.newPasswordConfirmation = newPasswordConfirmation;
    }

    public void setUserVerificationTokenManager(UserVerificationTokenManager userVerificationTokenManager) {
        this.userVerificationTokenManager = userVerificationTokenManager;
    }

    public CaptchaManager getCaptchaManager() {
        return this.captchaManager;
    }

    public void setCaptchaManager(CaptchaManager captchaManager) {
        this.captchaManager = captchaManager;
    }

    @Override
    public boolean mustValidateCaptcha() {
        return true;
    }
}

