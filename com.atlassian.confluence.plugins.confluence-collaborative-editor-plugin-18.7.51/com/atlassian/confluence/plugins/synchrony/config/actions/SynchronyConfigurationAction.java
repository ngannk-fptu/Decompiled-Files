/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.synchrony.config.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;

public class SynchronyConfigurationAction
extends ConfluenceActionSupport {
    public String doDefault() {
        return "btf";
    }

    public boolean isPermitted() {
        return this.permissionManager.isSystemAdministrator((User)AuthenticatedUserThreadLocal.get());
    }

    public String execute() {
        return "btf";
    }

    public String generate() {
        return "btf";
    }
}

