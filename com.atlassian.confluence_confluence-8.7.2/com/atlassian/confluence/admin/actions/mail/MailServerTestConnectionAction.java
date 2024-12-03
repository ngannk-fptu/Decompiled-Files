/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.MailProtocol
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.gson.Gson
 *  javax.mail.Folder
 *  javax.mail.Session
 *  javax.mail.Store
 *  javax.mail.URLName
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.actions.mail;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.admin.actions.mail.MailServerTestResult;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.mail.Authorization;
import com.atlassian.confluence.mail.ConfluenceMailServerBuilder;
import com.atlassian.confluence.mail.InboundMailServer;
import com.atlassian.confluence.oauth2.OAuth2Service;
import com.atlassian.confluence.security.InvalidOperationException;
import com.atlassian.mail.MailException;
import com.atlassian.mail.MailProtocol;
import com.atlassian.mail.server.MailServer;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.gson.Gson;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.naming.NamingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class MailServerTestConnectionAction
extends ConfluenceActionSupport {
    private static final long serialVersionUID = -3146579309722587783L;
    private static final Logger log = LoggerFactory.getLogger(MailServerTestConnectionAction.class);
    private MailServerTestResult testResult;
    private OAuth2Service oAuth2Service;
    private final Gson gson = new Gson();
    private String token;
    private String protocol;
    private String hostname;
    private String port;
    private String userName;
    private String authorization;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    @PermittedMethods(value={HttpMethod.POST})
    public String execute() throws Exception {
        Folder folder;
        Store store;
        if (this.getToken() == null) {
            log.error(String.format("Connection to mail server %s failed because the token is unavailable", this.getHostname()));
            this.testResult = new MailServerTestResult(MailServerTestResult.Status.FAILED, this.getText("setup.mail.server.test.connection.error"));
        } else {
            store = null;
            folder = null;
            MailServer mailServer = this.getMailServer();
            ClientTokenEntity token = this.getoAuth2Service().getToken(this.getToken());
            store = this.getSession(mailServer).getStore(new URLName(this.getProtocol(), this.getHostname(), Integer.parseInt(this.getPort()), null, this.getUserName(), token.getAccessToken()));
            store.connect(this.getHostname(), this.getUserName(), token.getAccessToken());
            if (mailServer instanceof InboundMailServer) {
                folder = store.getFolder("INBOX");
                folder.open(2);
            }
            this.testResult = new MailServerTestResult(MailServerTestResult.Status.OK, this.getText("setup.mail.server.test.connection.success"));
            try {
                this.closeQuietlyIfOpen(folder);
            }
            finally {
                this.closeQuietly(store);
            }
        }
        catch (Exception e) {
            try {
                this.testResult = new MailServerTestResult(MailServerTestResult.Status.FAILED, this.getText("setup.mail.server.test.connection.error"));
                log.error(String.format("Connection to mail server %s failed: %s", this.getHostname(), e.getMessage()), (Throwable)e);
            }
            catch (Throwable throwable) {
                try {
                    this.closeQuietlyIfOpen(folder);
                }
                finally {
                    this.closeQuietly(store);
                }
                throw throwable;
            }
            try {
                this.closeQuietlyIfOpen(folder);
            }
            finally {
                this.closeQuietly(store);
            }
        }
        return "json";
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHostname() {
        return this.hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAuthorization() {
        return this.authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public OAuth2Service getoAuth2Service() {
        return this.oAuth2Service;
    }

    public void setoAuth2Service(OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
    }

    @VisibleForTesting
    protected MailServer getMailServer() {
        MailProtocol mailProtocol = MailProtocol.getMailProtocol((String)this.protocol);
        if (mailProtocol == null) {
            throw new InvalidOperationException("No protocol is specified for the new mail server");
        }
        ConfluenceMailServerBuilder confluenceMailServerBuilder = ConfluenceMailServerBuilder.builder().hostName(this.getHostname()).port(this.getPort()).username(this.getUserName()).mailProtocol(mailProtocol);
        InboundMailServer mailServer = (InboundMailServer)confluenceMailServerBuilder.buildMailServer();
        String authorization = this.getAuthorization();
        if (!"BasicAuth".equals(authorization)) {
            Authorization.OAuth2 oAuth = new Authorization.OAuth2(authorization, this.getToken());
            mailServer.setAuthorization(oAuth);
        }
        return mailServer;
    }

    private Session getSession(MailServer mailServer) throws MailException {
        Properties props = new Properties();
        props.setProperty("mail.pop3s.auth.xoauth2.two.line.authentication.format", "true");
        props.setProperty("mail.pop3s.ssl.enable", "true");
        props.setProperty("mail.imap.ssl.enable", "true");
        props.setProperty("mail.pop3s.auth.mechanisms", "XOAUTH2");
        props.setProperty("mail.imaps.auth.mechanisms", "XOAUTH2");
        mailServer.setProperties(props);
        try {
            return mailServer.getSession();
        }
        catch (NamingException e) {
            throw new MailException((Throwable)e);
        }
    }

    private void closeQuietly(Store store) {
        if (store != null) {
            try {
                store.close();
            }
            catch (Exception e) {
                log.error("Failed to close mail session", (Throwable)e);
            }
        }
    }

    private void closeQuietlyIfOpen(Folder folder) {
        try {
            if (folder != null && !folder.isOpen()) {
                folder.close();
            }
        }
        catch (Exception e) {
            log.error("Failed to close mail folder", (Throwable)e);
        }
    }

    public String getJSONString() {
        return this.gson.toJson((Object)this.testResult);
    }
}

