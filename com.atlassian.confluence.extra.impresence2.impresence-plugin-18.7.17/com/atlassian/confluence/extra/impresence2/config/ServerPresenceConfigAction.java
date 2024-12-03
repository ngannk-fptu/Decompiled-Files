/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.RequireSecurityToken
 */
package com.atlassian.confluence.extra.impresence2.config;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.extra.impresence2.PresenceManager;
import com.atlassian.confluence.extra.impresence2.reporter.ServerPresenceReporter;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.RequireSecurityToken;

@WebSudoRequired
public abstract class ServerPresenceConfigAction
extends ConfluenceActionSupport {
    private String server;
    private PresenceManager presenceManager;

    public void setPresenceManager(PresenceManager presenceManager) {
        this.presenceManager = presenceManager;
    }

    public boolean isPermitted() {
        return super.isPermitted() && this.permissionManager.hasPermission(this.getRemoteUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public String doDefault() throws Exception {
        ServerPresenceReporter reporter = this.getReporter();
        if (null != reporter) {
            this.setServer(reporter.getServer());
        }
        return super.doDefault();
    }

    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        ServerPresenceReporter reporter = this.getReporter();
        if (reporter == null) {
            this.addActionError(this.getText("error.general.nosuchreporter", new String[]{this.getServiceName()}));
            return "error";
        }
        reporter.setServer(this.getServer());
        return "success";
    }

    protected abstract String getServiceKey();

    protected abstract String getServiceName();

    public String getServer() {
        return this.server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getActionName(String fullClassName) {
        return this.getText("com.atlassian.confluence.extra.impresence2.config.ServerPresenceConfigAction.name", new String[]{this.getServiceName()});
    }

    public ServerPresenceReporter getReporter() {
        return (ServerPresenceReporter)this.presenceManager.getReporter(this.getServiceKey());
    }

    public boolean isSystemAdministrator() {
        return this.permissionManager.hasPermission(this.getRemoteUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }
}

