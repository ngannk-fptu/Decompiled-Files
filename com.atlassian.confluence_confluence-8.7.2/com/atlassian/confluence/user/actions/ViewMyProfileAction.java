/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.List;

public class ViewMyProfileAction
extends AbstractUserProfileAction
implements FormAware {
    private CaptchaManager captchaManager;

    @PermittedMethods(value={HttpMethod.GET})
    public String doView() {
        return "success";
    }

    public List<String> getUserDetailsKeys(String groupKey) {
        return this.userDetailsManager.getProfileKeys(groupKey);
    }

    public List<String> getUserDetailsGroups() {
        return this.userDetailsManager.getProfileGroups();
    }

    @Override
    public boolean isEditMode() {
        return false;
    }

    @Override
    public boolean isPermitted() {
        return this.getUsername() != null && super.isPermitted();
    }

    public CaptchaManager getCaptchaManager() {
        return this.captchaManager;
    }

    public void setCaptchaManager(CaptchaManager captchaManager) {
        this.captchaManager = captchaManager;
    }
}

