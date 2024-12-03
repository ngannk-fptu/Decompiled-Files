/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.admin.actions.mail;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.List;

@WebSudoRequired
@SystemAdminOnly
public class ViewMailServersAction
extends ConfluenceActionSupport {
    private MailServerManager mailServerManager;
    private List<MailServer> mailServers;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public List<MailServer> getMailServers() {
        if (this.mailServers == null) {
            this.mailServers = new ArrayList<MailServer>();
            this.mailServers.addAll(this.mailServerManager.getSmtpMailServers());
            this.mailServers.addAll(this.mailServerManager.getPopMailServers());
            this.mailServers.addAll(this.mailServerManager.getImapMailServers());
        }
        return this.mailServers;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public void setMailServerManager(MailServerManager mailServerManager) {
        this.mailServerManager = mailServerManager;
    }
}

