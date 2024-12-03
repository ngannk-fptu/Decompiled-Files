/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.InboundMailServer
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.emailgateway.actions;

import com.atlassian.confluence.mail.InboundMailServer;
import com.atlassian.confluence.plugins.emailgateway.api.EmailGatewaySettingsManager;
import com.atlassian.confluence.plugins.emailgateway.api.InboundMailServerManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction;
import com.atlassian.mail.server.MailServer;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;

@WebSudoRequired
public class EmailGatewayInfoAction
extends AbstractSpaceAdminAction {
    private EmailGatewaySettingsManager emailGatewaySettingsManager;
    private InboundMailServerManager inboundMailServerManager;

    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, (Object)this.getSpace());
    }

    public String doDefault() throws Exception {
        if (this.getSpace() == null || !this.getSpace().isPersonal()) {
            return "pagenotfound";
        }
        return super.doDefault();
    }

    public void setEmailGatewaySettingsManager(EmailGatewaySettingsManager emailGatewaySettingsManager) {
        this.emailGatewaySettingsManager = emailGatewaySettingsManager;
    }

    public void setInboundMailServerManager(InboundMailServerManager inboundMailServerManager) {
        this.inboundMailServerManager = inboundMailServerManager;
    }

    public String getEmail() {
        MailServer mailServer = this.inboundMailServerManager.getMailServer();
        if (mailServer instanceof InboundMailServer) {
            return ((InboundMailServer)mailServer).getToAddress();
        }
        return null;
    }

    public boolean isAllowToCreateCommentByEmail() {
        return this.emailGatewaySettingsManager.isAllowToCreateCommentByEmail();
    }

    public boolean isAllowToCreatePageByEmail() {
        return this.emailGatewaySettingsManager.isAllowToCreatePageByEmail();
    }
}

