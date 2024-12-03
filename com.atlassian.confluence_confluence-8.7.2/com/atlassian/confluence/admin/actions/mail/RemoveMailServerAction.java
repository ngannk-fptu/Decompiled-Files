/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.MailException
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
import com.atlassian.mail.MailException;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Collection;

@WebSudoRequired
@SystemAdminOnly
public class RemoveMailServerAction
extends ConfluenceActionSupport {
    private Long id;
    private MailServerManager mailServerManager;

    public Collection getSmtpMailServers() throws MailException {
        return this.mailServerManager.getSmtpMailServers();
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String execute() throws Exception {
        this.mailServerManager.delete(this.getId());
        return "success";
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public void setMailServerManager(MailServerManager mailServerManager) {
        this.mailServerManager = mailServerManager;
    }
}

