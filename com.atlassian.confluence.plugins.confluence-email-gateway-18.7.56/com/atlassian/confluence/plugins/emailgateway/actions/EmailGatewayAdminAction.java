/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.plugins.emailgateway.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.plugins.emailgateway.api.EmailGatewaySettingsManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmailGatewayAdminAction
extends ConfluenceActionSupport {
    private boolean allowToCreatePageByEmail;
    private boolean allowToCreateCommentByEmail;
    private long selectedMailServerId;
    private MailServer mailServer;
    private EmailGatewaySettingsManager emailGatewaySettingsManager;
    private MailServerManager mailServerManager;
    private boolean updated = false;
    private boolean error = false;

    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        this.readConfigData();
        return "success";
    }

    @PermittedMethods(value={HttpMethod.POST})
    public String processSubmit() {
        this.applyFormData();
        if (this.mailServer != null) {
            this.emailGatewaySettingsManager.setAllowToCreatePageByEmail(this.allowToCreatePageByEmail);
            this.emailGatewaySettingsManager.setAllowToCreateCommentByEmail(this.allowToCreateCommentByEmail);
            this.emailGatewaySettingsManager.setDefaultMailServer(this.mailServer);
            return "success";
        }
        return "error";
    }

    public boolean isAllowToCreatePageByEmail() {
        return this.allowToCreatePageByEmail;
    }

    public void setAllowToCreatePageByEmail(boolean allowToCreatePageByEmail) {
        this.allowToCreatePageByEmail = allowToCreatePageByEmail;
    }

    public boolean isAllowToCreateCommentByEmail() {
        return this.allowToCreateCommentByEmail;
    }

    public void setAllowToCreateCommentByEmail(boolean allowToCreateCommentByEmail) {
        this.allowToCreateCommentByEmail = allowToCreateCommentByEmail;
    }

    public long getSelectedMailServerId() {
        return this.selectedMailServerId;
    }

    public void setSelectedMailServerId(long selectedMailServerId) {
        this.selectedMailServerId = selectedMailServerId;
    }

    public MailServer getMailServer() {
        return this.mailServer;
    }

    public void setMailServer(MailServer mailServer) {
        this.mailServer = mailServer;
    }

    public void setEmailGatewaySettingsManager(EmailGatewaySettingsManager emailGatewaySettingsManager) {
        this.emailGatewaySettingsManager = emailGatewaySettingsManager;
    }

    public void setMailServerManager(MailServerManager mailServerManager) {
        this.mailServerManager = mailServerManager;
    }

    public boolean isUpdated() {
        return this.updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public boolean isError() {
        return this.error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    private void readConfigData() {
        this.allowToCreatePageByEmail = this.emailGatewaySettingsManager.isAllowToCreatePageByEmail();
        this.allowToCreateCommentByEmail = this.emailGatewaySettingsManager.isAllowToCreateCommentByEmail();
        this.mailServer = this.emailGatewaySettingsManager.getDefaultInboundMailServer();
        if (this.mailServer == null) {
            this.mailServer = this.mailServerManager.getDefaultPopMailServer();
        } else {
            Optional<MailServer> selectedMailServer = this.getAllInboundMailServers().stream().filter(ms -> ms.getId().equals(this.mailServer.getId())).findFirst();
            this.mailServer = selectedMailServer.orElse((MailServer)this.mailServerManager.getDefaultPopMailServer());
        }
        if (this.mailServer != null) {
            this.selectedMailServerId = this.mailServer.getId();
        }
    }

    public List<MailServer> getAllInboundMailServers() {
        ArrayList<MailServer> mailServers = new ArrayList<MailServer>();
        mailServers.addAll(this.mailServerManager.getPopMailServers());
        mailServers.addAll(this.mailServerManager.getImapMailServers());
        return mailServers;
    }

    private void applyFormData() {
        Optional<MailServer> selectedMailServer = this.getAllInboundMailServers().stream().filter(ms -> ms.getId().equals(this.selectedMailServerId)).findFirst();
        this.mailServer = selectedMailServer.orElse(null);
    }
}

