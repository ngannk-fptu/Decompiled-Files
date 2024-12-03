/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.access.annotations.RequiresLicensedConfluenceAccess
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.plugins.user.profile;

import com.atlassian.confluence.plugins.user.profile.AbstractUserProfileAction;
import com.atlassian.confluence.security.access.annotations.RequiresLicensedConfluenceAccess;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@RequiresLicensedConfluenceAccess
public class EditMyProfilePictureAction
extends AbstractUserProfileAction {
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public boolean isPermitted() {
        return this.getAuthenticatedUser() != null;
    }
}

