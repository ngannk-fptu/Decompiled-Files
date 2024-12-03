/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.actions.debug;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.pages.ancestors.AncestorRebuildException;
import com.atlassian.confluence.pages.ancestors.PageAncestorManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class RebuildAncestorTableAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(RebuildAncestorTableAction.class);
    private PageAncestorManager pageAncestorManager;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public String doRebuildAncestorTable() {
        try {
            this.pageAncestorManager.rebuildAll();
        }
        catch (AncestorRebuildException e) {
            log.error("Error rebuilding ancestor table", (Throwable)e);
            this.getActionErrors().add(e.getMessage());
            return "error";
        }
        return "success";
    }

    public void setPageAncestorManager(PageAncestorManager pageAncestorManager) {
        this.pageAncestorManager = pageAncestorManager;
    }
}

