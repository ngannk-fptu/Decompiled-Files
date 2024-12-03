/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.user.SignupManager;
import com.atlassian.confluence.user.SignupValidator;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.confluence.validation.MessageHolder;
import org.apache.commons.lang3.StringUtils;

public class DefaultSignupValidator
implements SignupValidator {
    private final SignupManager easyUserManager;
    private final UserChecker userChecker;
    private final BootstrapManager bootstrapManager;

    public DefaultSignupValidator(SignupManager easyUserManager, UserChecker userChecker, BootstrapManager bootstrapManager) {
        this.easyUserManager = easyUserManager;
        this.userChecker = userChecker;
        this.bootstrapManager = bootstrapManager;
    }

    @Override
    public void validateSignup(String privateToken, MessageHolder holder) {
        if (!this.isLicensedToAddMoreUsers()) {
            holder.addActionError("not.licensed", this.getContactAdminUrl());
            return;
        }
        if (this.easyUserManager.isPublicSignupPermitted()) {
            return;
        }
        if (this.easyUserManager.isDomainRestrictedSignupEnabled()) {
            return;
        }
        if (StringUtils.isBlank((CharSequence)privateToken)) {
            holder.addActionError("public.signup.disabled", this.getContactAdminUrl());
            return;
        }
        if (!this.easyUserManager.canSignUpWith(privateToken)) {
            holder.addActionError("signup.token.expired", this.getContactAdminUrl());
        }
    }

    private boolean isLicensedToAddMoreUsers() {
        return this.userChecker.isLicensedToAddMoreUsers();
    }

    private String[] getContactAdminUrl() {
        return new String[]{this.bootstrapManager.getWebAppContextPath() + "/administrators.action"};
    }
}

