/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  com.atlassian.sal.api.permission.NotAuthenticatedException
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.core.permission.DefaultPermissionEnforcer
 */
package com.atlassian.sal.confluence.permission;

import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.AuthorisationException;
import com.atlassian.sal.api.permission.NotAuthenticatedException;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.core.permission.DefaultPermissionEnforcer;

public class ConfluencePermissionEnforcer
extends DefaultPermissionEnforcer {
    private final I18nResolver i18nResolver;

    public ConfluencePermissionEnforcer(UserManager userManager, I18nResolver i18nResolver) {
        super(userManager);
        this.i18nResolver = i18nResolver;
    }

    protected void throwNotAuthorised(String message) {
        String confluenceMessage = this.i18nResolver.getText("confluence.service.accessdenied");
        throw new AuthorisationException(confluenceMessage, (Throwable)new PermissionException(confluenceMessage));
    }

    protected void throwNotAuthenticated(String message) {
        String confluenceMessage = this.i18nResolver.getText("confluence.service.accessdenied");
        throw new NotAuthenticatedException(confluenceMessage, (Throwable)new PermissionException(confluenceMessage));
    }
}

