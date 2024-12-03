/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.RequireSecurityToken
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.impresence2.config;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.extra.impresence2.PresenceManager;
import com.atlassian.confluence.extra.impresence2.reporter.LoginPresenceReporter;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.RequireSecurityToken;
import org.apache.commons.lang3.StringUtils;

@WebSudoRequired
public abstract class LoginPresenceConfigAction
extends ConfluenceActionSupport {
    private String reporterId;
    private String reporterPassword;
    private PresenceManager presenceManager;

    public void setPresenceManager(PresenceManager presenceManager) {
        this.presenceManager = presenceManager;
    }

    public boolean isPermitted() {
        return super.isPermitted() && this.permissionManager.hasPermission(this.getRemoteUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public String doDefault() throws Exception {
        LoginPresenceReporter reporter = this.getReporter();
        if (null != reporter) {
            this.setReporterId(reporter.getId());
            this.setReporterPassword(reporter.getPassword());
        }
        return super.doDefault();
    }

    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        LoginPresenceReporter reporter = this.getReporter();
        if (reporter == null) {
            this.addActionError(this.getText("error.general.nosuchreporter", new String[]{this.getServiceName()}));
            return "error";
        }
        reporter.setId(this.getReporterId());
        reporter.setPassword(this.getReporterPassword());
        return "success";
    }

    protected abstract String getServiceKey();

    protected abstract String getServiceName();

    public String getReporterId() {
        return this.reporterId;
    }

    public String getReporterPassword() {
        return this.reporterPassword;
    }

    public String getReporterPasswordPlaceholder() {
        if (StringUtils.isEmpty((CharSequence)this.reporterPassword)) {
            return "";
        }
        return "********";
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public void setReporterPassword(String reporterPassword) {
        this.reporterPassword = reporterPassword;
    }

    public String getActionName(String fullClassName) {
        return this.getText("com.atlassian.confluence.extra.impresence2.config.LoginPresenceConfigAction.name", new String[]{this.getServiceName()});
    }

    public LoginPresenceReporter getReporter() {
        return (LoginPresenceReporter)this.presenceManager.getReporter(this.getServiceKey());
    }

    public boolean isSystemAdministrator() {
        return this.permissionManager.hasPermission(this.getRemoteUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }
}

