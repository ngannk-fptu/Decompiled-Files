/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.impl.search.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.internal.search.IncrementalIndexManager;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public abstract class AbstractFlushIndexQueueAction
extends ConfluenceActionSupport {
    protected IncrementalIndexManager indexManager;

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String execute() throws Exception {
        this.indexManager.flushQueue(IndexManager.IndexQueueFlushMode.ENTIRE_QUEUE);
        return "success";
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }
}

