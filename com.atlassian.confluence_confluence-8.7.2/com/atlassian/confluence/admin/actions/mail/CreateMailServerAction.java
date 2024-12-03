/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.actions.mail;

import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.setup.actions.AbstractSetupEmailAction;
import com.atlassian.mail.MailException;
import com.atlassian.mail.server.MailServer;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class CreateMailServerAction
extends AbstractSetupEmailAction {
    private static final long serialVersionUID = 4053010379617364994L;
    private static final Logger log = LoggerFactory.getLogger(CreateMailServerAction.class);
    private static final String ACTION_NAME = "create";

    @Override
    public void validate() {
        super.validate();
        try {
            if (this.getMailServerManager().getMailServer(this.getName()) != null) {
                this.addFieldError("name", "setup.mail.server.already.exists", new Object[]{this.getName()});
            }
        }
        catch (MailException e) {
            log.error("Error occurred while validating mail server by name: {}", (Object)this.getName(), (Object)e);
        }
    }

    @Override
    public String doDefaultInternal() {
        return "input";
    }

    @Override
    public String getActionName() {
        return ACTION_NAME;
    }

    @Override
    protected String executeInternal() throws Exception {
        MailServer mailServer = this.getMailServer();
        this.getMailServerManager().create(mailServer);
        return "success";
    }

    @Override
    public String buildRedirect(String flowId) {
        return String.format("%s/admin/mail/createmailserver.action?protocol=%s&flowId=%s", this.getApplicationProperties().getBaseUrl(UrlMode.RELATIVE), this.getProtocol(), flowId);
    }
}

