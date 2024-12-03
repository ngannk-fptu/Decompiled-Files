/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.MailProtocol
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.mail.server.SMTPMailServer
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Strings
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.actions.mail;

import com.atlassian.confluence.event.events.admin.MailServerEditEvent;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.jmx.JmxSMTPMailServer;
import com.atlassian.confluence.mail.Authorization;
import com.atlassian.confluence.mail.InboundMailServer;
import com.atlassian.confluence.setup.actions.AbstractSetupEmailAction;
import com.atlassian.confluence.util.HTMLPairType;
import com.atlassian.event.Event;
import com.atlassian.mail.MailException;
import com.atlassian.mail.MailProtocol;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class EditMailServerAction
extends AbstractSetupEmailAction {
    private static final long serialVersionUID = -3146579309722587783L;
    private static final Logger log = LoggerFactory.getLogger(EditMailServerAction.class);
    private static final String ACTION_NAME = "edit";
    @VisibleForTesting
    static final String PASSWORD_PLACEHOLDER = "0e3e77a03cb9e50d5206be8e72af298c";

    @Override
    public void validate() {
        super.validate();
        try {
            MailServer mailServer = this.getMailServerManager().getMailServer(this.getId());
            if (mailServer == null) {
                this.addActionError(this.getText("setup.mail.server.not.exists", new String[]{String.valueOf(this.getId())}));
                return;
            }
            String originalName = mailServer.getName();
            if (!originalName.equals(this.getName()) && this.getMailServerManager().getMailServer(this.getName()) != null) {
                this.addFieldError("name", "setup.mail.server.already.exists", new Object[]{this.getName()});
            }
        }
        catch (MailException e) {
            this.addActionError(this.getText("setup.mail.server.not.exists", new String[]{String.valueOf(this.getId())}));
            log.error("Error retrieving Mail Server with id: " + this.getId());
        }
    }

    @Override
    public String getActionName() {
        return ACTION_NAME;
    }

    @Override
    public String doDefaultInternal() throws Exception {
        MailServer mailServer = this.getMailServerManager().getMailServer(this.getId());
        if (mailServer == null) {
            this.addActionError(this.getText("setup.mail.server.not.exists", new String[]{String.valueOf(this.getId())}));
            return "input";
        }
        this.setName(mailServer.getName());
        if (StringUtils.isNotBlank((CharSequence)mailServer.getHostname()) && StringUtils.isNotBlank((CharSequence)mailServer.getPort())) {
            this.setHostname(mailServer.getHostname());
            this.setPort(mailServer.getPort());
        }
        this.setUserName(mailServer.getUsername());
        this.setPassword(Strings.isNullOrEmpty((String)mailServer.getPassword()) ? "" : PASSWORD_PLACEHOLDER);
        if (mailServer instanceof SMTPMailServer) {
            SMTPMailServer smtpMailServer = (SMTPMailServer)mailServer;
            this.setEmailAddress(smtpMailServer.getDefaultFrom());
            this.setPrefix(smtpMailServer.getPrefix());
            this.setJndiName(smtpMailServer.getJndiLocation());
            this.setTlsRequired(smtpMailServer.isTlsRequired());
            if (smtpMailServer instanceof JmxSMTPMailServer) {
                this.setFromName(((JmxSMTPMailServer)smtpMailServer).getFromName());
            }
            this.setProtocol("smtp");
        } else if (mailServer instanceof InboundMailServer) {
            Authorization currentAuthorization;
            InboundMailServer inboundMailServer = (InboundMailServer)mailServer;
            this.setEmailAddress(inboundMailServer.getToAddress());
            this.setProtocol(inboundMailServer.getMailProtocol().getProtocol());
            if (this.getFlowId() == null && (currentAuthorization = inboundMailServer.getAuthorization()) != null) {
                this.setAuthorization(currentAuthorization.getProviderId());
                this.setToken(currentAuthorization.getTokenId());
            }
        }
        return "input";
    }

    @Override
    public List<HTMLPairType> getAuthorizationList() {
        List<HTMLPairType> result = super.getAuthorizationList();
        String authorization = this.getAuthorization();
        if (authorization != null && result.stream().filter(pair -> pair.getKey().toString().equals(authorization)).findFirst().isEmpty()) {
            result.add(new HTMLPairType(authorization, this.getText("mailserver.unknown")));
        }
        return result;
    }

    @Override
    protected String executeInternal() throws Exception {
        MailServer mailServer = this.getMailServerManager().getMailServer(this.getId());
        if (mailServer == null) {
            log.warn("Mail server with id {} not found", (Object)this.getId());
            this.addActionError(this.getText("setup.mail.server.not.exists", new String[]{String.valueOf(this.getId())}));
            return "input";
        }
        String originalName = mailServer.getName();
        mailServer.setName(this.getName());
        mailServer.setUsername(this.getUserName());
        if (!PASSWORD_PLACEHOLDER.equals(this.getPassword())) {
            mailServer.setPassword(this.getPassword());
        }
        if ("smtp".equals(this.protocol)) {
            SMTPMailServer smtpMailServer = (SMTPMailServer)mailServer;
            smtpMailServer.setDefaultFrom(this.getEmailAddress());
            smtpMailServer.setPrefix(this.getPrefix());
            if (StringUtils.isNotEmpty((CharSequence)this.getJndiName())) {
                smtpMailServer.setJndiLocation(this.getJndiName());
                smtpMailServer.setSessionServer(true);
                smtpMailServer.setHostname(null);
            } else if (StringUtils.isNotBlank((CharSequence)this.getHostname())) {
                smtpMailServer.setHostname(this.getHostname());
                smtpMailServer.setPort(this.getPort());
                smtpMailServer.setSessionServer(false);
                smtpMailServer.setJndiLocation(null);
                smtpMailServer.setTlsRequired(this.isTlsRequired());
                if (!this.isTlsRequired()) {
                    smtpMailServer.getProperties().remove("mail.smtp.starttls.enable");
                }
                if (StringUtils.isBlank((CharSequence)this.getUserName())) {
                    smtpMailServer.getProperties().remove("mail.smtp.auth");
                }
            }
            if (smtpMailServer instanceof JmxSMTPMailServer) {
                ((JmxSMTPMailServer)smtpMailServer).setFromName(this.getFromName());
            }
        } else {
            mailServer.setHostname(this.getHostname());
            mailServer.setPort(this.getPort());
            mailServer.setMailProtocol(MailProtocol.getMailProtocol((String)this.protocol));
            InboundMailServer inboundMailServer = (InboundMailServer)mailServer;
            inboundMailServer.setToAddress(this.getEmailAddress());
            String authentication = this.getAuthorization();
            if (!"BasicAuth".equals(authentication)) {
                Authorization.OAuth2 oAuth = new Authorization.OAuth2(authentication, this.getToken());
                inboundMailServer.setAuthorization(oAuth);
            } else {
                inboundMailServer.setAuthorization(null);
            }
        }
        this.getMailServerManager().update(mailServer);
        this.eventManager.publishEvent((Event)new MailServerEditEvent(this, mailServer, originalName));
        return "success";
    }

    @Override
    public String buildRedirect(String flowId) {
        return String.format("%s/admin/mail/editmailserver.action?id=%s&flowId=%s", this.getApplicationProperties().getBaseUrl(UrlMode.RELATIVE), this.getId(), flowId);
    }
}

