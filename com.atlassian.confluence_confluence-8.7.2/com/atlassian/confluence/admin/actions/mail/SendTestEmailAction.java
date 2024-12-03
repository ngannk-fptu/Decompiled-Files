/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.mail.server.SMTPMailServer
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  javax.mail.AuthenticationFailedException
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.exception.ExceptionUtils
 */
package com.atlassian.confluence.admin.actions.mail;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.mail.Email;
import com.atlassian.mail.MailException;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import java.util.HashMap;
import java.util.Map;
import javax.mail.AuthenticationFailedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

@WebSudoRequired
@SystemAdminOnly
public class SendTestEmailAction
extends ConfluenceActionSupport {
    private Long id;
    private String to;
    private String subject;
    private String messageType;
    private String message;
    private String logMessage;
    private MailServerManager mailServerManager;
    private static final String NO_AUTH_MECHANISMS_HINT = "An error has occurred with sending the test email. If your mail server requires authentication, please ensure that TLS or SSL is enabled on your server for the specific mailbox. If your mail server allows anonymous connection, please remove the username and password from the configuration and try again.";

    @Override
    public String doDefault() throws Exception {
        String from = null;
        String servername = null;
        String username = null;
        SMTPMailServer mailserver = this.getSmtpMailServer();
        from = mailserver.getDefaultFrom();
        servername = mailserver.getName();
        username = mailserver.getUsername();
        this.setMessage("This is a test message from Confluence. \nServer: " + servername + "\nFrom: " + from + "\nHost User Name: " + username);
        this.setSubject("Test Message From Confluence");
        this.setMessageType("text");
        this.setTo(this.getAuthenticatedUser().getEmail());
        return "input";
    }

    public String execute() throws Exception {
        try {
            Email mail = new Email(this.to);
            mail.setBody(this.message);
            mail.setSubject(this.subject);
            if ("html".equals(this.getMessageType())) {
                mail.setMimeType("text/html");
            }
            this.getSmtpMailServer().send(mail);
        }
        catch (Exception e) {
            Throwable t = ExceptionUtils.getRootCause((Throwable)e);
            Throwable cause = t != null ? t : e;
            String message = cause.getMessage();
            this.logMessage = cause instanceof AuthenticationFailedException && (StringUtils.containsIgnoreCase((CharSequence)message, (CharSequence)"mechanisms") || StringUtils.containsIgnoreCase((CharSequence)message, (CharSequence)"mechansims")) ? "An error has occurred with sending the test email. If your mail server requires authentication, please ensure that TLS or SSL is enabled on your server for the specific mailbox. If your mail server allows anonymous connection, please remove the username and password from the configuration and try again.\n\n" + ExceptionUtils.getStackTrace((Throwable)e) : "An error has occurred with sending the test email:\n" + ExceptionUtils.getStackTrace((Throwable)e);
            return "input";
        }
        this.logMessage = "Your test message has been sent successfully to " + this.to + ".";
        return "input";
    }

    public Map getMimeTypes() {
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("html", "Html");
        result.put("text", "Text");
        return result;
    }

    private SMTPMailServer getSmtpMailServer() throws MailException {
        return (SMTPMailServer)this.mailServerManager.getMailServer(this.getId());
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTo() {
        return this.to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLog() {
        return this.logMessage;
    }

    public void setMailServerManager(MailServerManager mailServerManager) {
        this.mailServerManager = mailServerManager;
    }
}

