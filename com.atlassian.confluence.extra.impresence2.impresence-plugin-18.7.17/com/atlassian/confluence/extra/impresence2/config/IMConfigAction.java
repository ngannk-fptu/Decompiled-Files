/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 */
package com.atlassian.confluence.extra.impresence2.config;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.extra.impresence2.PresenceManager;
import com.atlassian.confluence.extra.impresence2.reporter.PresenceReporter;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import java.util.Collection;

@WebSudoRequired
public class IMConfigAction
extends ConfluenceActionSupport {
    private PresenceManager presenceManager;

    public void setPresenceManager(PresenceManager presenceManager) {
        this.presenceManager = presenceManager;
    }

    public boolean isPermitted() {
        return super.isPermitted() && this.permissionManager.hasPermission(this.getRemoteUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public Collection<PresenceReporter> getReporters() {
        return this.presenceManager.getReporters();
    }

    public boolean isSystemAdministrator() {
        return this.permissionManager.hasPermission(this.getRemoteUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }
}

