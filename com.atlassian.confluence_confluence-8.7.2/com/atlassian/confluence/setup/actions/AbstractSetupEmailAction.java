/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.MailProtocol
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.mail.server.SMTPMailServer
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.mail.Authorization;
import com.atlassian.confluence.mail.ConfluenceMailServerBuilder;
import com.atlassian.confluence.mail.ConfluencePopMailServer;
import com.atlassian.confluence.mail.InboundMailServer;
import com.atlassian.confluence.oauth2.OAuth2Exception;
import com.atlassian.confluence.oauth2.OAuth2Service;
import com.atlassian.confluence.security.InvalidOperationException;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.util.HTMLPairType;
import com.atlassian.mail.MailProtocol;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSetupEmailAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(AbstractSetupEmailAction.class);
    @VisibleForTesting
    static final String BASIC_AUTH_VALUE_KEY = "basic.auth";
    @VisibleForTesting
    public static final String OAUTH_FLOW_SESSION_KEY = "oauth-flow-%s";
    @VisibleForTesting
    public static final String BASIC_AUTH_KEY = "BasicAuth";
    private static final String JNDI_JAVA_SCHEME = "java:";
    private String name;
    private String emailAddress;
    private String fromName = "${fullname} (Confluence)";
    private String prefix = "[confluence]";
    private String userName;
    private String password;
    private String hostname;
    private String port = "25";
    private String jndiName;
    private Long id;
    private MailServerManager mailServerManager;
    private OAuth2Service oAuth2Service;
    private ApplicationProperties applicationProperties;
    protected String protocol = "smtp";
    private boolean tls;
    private String testConnection;
    private String authorize;
    private String authorization;
    private String token;
    private String flowId;

    @Override
    public final boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    @Override
    public void validate() {
        if (StringUtils.isBlank((CharSequence)this.name)) {
            this.addFieldError("name", this.getText("AbstractSetupEmailAction.name.empty"));
        }
        if (StringUtils.isBlank((CharSequence)this.emailAddress)) {
            this.addFieldError("emailAddress", this.getText("AbstractSetupEmailAction.email.address.empty"));
        }
        if ("smtp".equals(this.protocol) && StringUtils.isBlank((CharSequence)this.prefix)) {
            this.addFieldError("prefix", this.getText("AbstractSetupEmailAction.prefix.empty"));
        }
        boolean hostNameIsBlank = StringUtils.isBlank((CharSequence)this.getHostname());
        boolean jndiNameIsBlank = StringUtils.isBlank((CharSequence)this.getJndiName());
        if (hostNameIsBlank && jndiNameIsBlank) {
            this.addActionError(this.getText("setup.mail.server.not.specified"));
        } else if (!hostNameIsBlank && !jndiNameIsBlank) {
            this.addActionError(this.getText("setup.mail.servers.both.specified"));
        } else if (!hostNameIsBlank) {
            if (StringUtils.isBlank((CharSequence)this.getPort())) {
                this.addFieldError("port", this.getText("mail.server.port.blank"));
            } else {
                try {
                    Integer.parseInt(this.getPort());
                }
                catch (NumberFormatException e) {
                    this.addFieldError("port", this.getText("mail.server.port.is.not.a.number"));
                }
            }
        } else {
            this.validateJNDIScheme();
        }
    }

    @Override
    public String doDefault() throws Exception {
        MailProtocol mailProtocol = MailProtocol.getMailProtocol((String)this.getProtocol());
        this.setPort(mailProtocol.getDefaultPort());
        if (this.getFlowId() != null) {
            this.restoreFromSession();
            try {
                this.setToken(this.getOAuth2Service().completeOAuth2Flow(this.getCurrentSession(), this.getAuthorization()));
                this.addActionMessage(this.getText("setup.mail.server.oauth2.complete"));
            }
            catch (OAuth2Exception oAuth2Exception) {
                this.addActionError(this.getText("setup.mail.server.configuration.error", new String[]{mailProtocol.getProtocol().toUpperCase(), mailProtocol.getDefaultPort()}));
                log.error("Could not set the token or test the connection for {}", (Object)mailProtocol, (Object)oAuth2Exception);
            }
        }
        return this.doDefaultInternal();
    }

    protected abstract String doDefaultInternal() throws Exception;

    public String execute() throws Exception {
        HttpSession httpSession = this.getCurrentSession();
        String auth = this.getAuthorization();
        if (auth != null && !BASIC_AUTH_KEY.equals(auth) && this.getAuthorize() != null) {
            try {
                OAuth2Service.OAuth2Result oAuth2Result = this.getOAuth2Service().initialiseOAuth2Flow(httpSession, this.authorization, this::buildRedirect);
                this.storeSessionAttributes(httpSession, oAuth2Result.getFlowId());
                return this.redirectUrl(oAuth2Result.getRedirectUrl());
            }
            catch (IllegalArgumentException illegalArgumentException) {
                this.addActionError(this.getText("setup.mail.servers.authorize.error"));
                return "input";
            }
        }
        return this.executeInternal();
    }

    public MailServer getMailServer() {
        String authorization;
        MailProtocol mailProtocol = MailProtocol.getMailProtocol((String)this.protocol);
        if (mailProtocol == null) {
            throw new InvalidOperationException("No protocol is specified for the new mail server");
        }
        boolean isOutboundMailServer = mailProtocol == MailProtocol.SMTP || mailProtocol == MailProtocol.SECURE_SMTP;
        ConfluenceMailServerBuilder confluenceMailServerBuilder = ConfluenceMailServerBuilder.builder().name(this.getName()).hostName(this.getHostname()).port(this.getPort()).username(this.getUserName()).password(this.getPassword()).emailAddress(this.getEmailAddress()).mailProtocol(mailProtocol);
        if (isOutboundMailServer) {
            confluenceMailServerBuilder.jndiName(this.getJndiName()).prefix(this.getPrefix()).fromName(this.getFromName()).tlsRequired(this.isTlsRequired());
        }
        MailServer mailServer = confluenceMailServerBuilder.buildMailServer();
        if (!isOutboundMailServer && !BASIC_AUTH_KEY.equals(authorization = this.getAuthorization()) && mailServer instanceof InboundMailServer) {
            Authorization.OAuth2 oAuth = new Authorization.OAuth2(authorization, this.getToken());
            ((InboundMailServer)mailServer).setAuthorization(oAuth);
        }
        return mailServer;
    }

    protected abstract String executeInternal() throws Exception;

    public String getName() {
        if (StringUtils.isNotBlank((CharSequence)this.name)) {
            return this.name;
        }
        return this.getText(String.format("default.%s.server.name", this.protocol));
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFromName() {
        return this.fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private SMTPMailServer getSmtpMailServer() {
        return this.getMailServerManager().getDefaultSMTPMailServer();
    }

    private ConfluencePopMailServer getPopMailServer() {
        return (ConfluencePopMailServer)this.getMailServerManager().getDefaultPopMailServer();
    }

    public String getHostname() {
        return this.hostname;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public boolean isTlsRequired() {
        return this.tls;
    }

    public void setTlsRequired(boolean tls) {
        this.tls = tls;
    }

    public String getJndiName() {
        return this.jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getAuthorization() {
        return this.authorization;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getFlowId() {
        return this.flowId;
    }

    public String getAuthorize() {
        return this.authorize;
    }

    public void setAuthorize(String authorize) {
        this.authorize = authorize;
    }

    public void setTestConnection(String testConnection) {
        this.testConnection = testConnection;
    }

    public String getTestConnection() {
        return this.testConnection;
    }

    public void setMailServerManager(MailServerManager mailServerManager) {
        this.mailServerManager = mailServerManager;
    }

    protected MailServerManager getMailServerManager() {
        return this.mailServerManager;
    }

    public OAuth2Service getOAuth2Service() {
        return this.oAuth2Service;
    }

    public void setoAuth2Service(OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
    }

    public ApplicationProperties getApplicationProperties() {
        return this.applicationProperties;
    }

    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public List<HTMLPairType> getIncomingProtocolList() {
        ArrayList<HTMLPairType> result = new ArrayList<HTMLPairType>();
        if (this.protocol.contains("pop3")) {
            result.add(new HTMLPairType(MailProtocol.POP.getProtocol(), MailProtocol.POP.getProtocol().toUpperCase()));
            result.add(new HTMLPairType(MailProtocol.SECURE_POP.getProtocol(), MailProtocol.SECURE_POP.getProtocol().toUpperCase()));
        } else if (this.protocol.contains("imap")) {
            result.add(new HTMLPairType(MailProtocol.IMAP.getProtocol(), MailProtocol.IMAP.getProtocol().toUpperCase()));
            result.add(new HTMLPairType(MailProtocol.SECURE_IMAP.getProtocol(), MailProtocol.SECURE_IMAP.getProtocol().toUpperCase()));
        }
        return result;
    }

    public List<HTMLPairType> getAuthorizationList() {
        ArrayList<HTMLPairType> result = new ArrayList<HTMLPairType>();
        result.add(new HTMLPairType(BASIC_AUTH_KEY, this.getText(BASIC_AUTH_VALUE_KEY)));
        result.addAll(this.getOAuth2Configurations());
        return result;
    }

    private List<HTMLPairType> getOAuth2Configurations() {
        return this.getOAuth2Service().getConfiguredOAuth2Providers().stream().map(entity -> new HTMLPairType(entity.getId(), entity.toString())).collect(Collectors.toList());
    }

    private void storeSessionAttributes(HttpSession currentSession, String flowId) {
        FormData formData = new FormData(this);
        currentSession.setAttribute(String.format(OAUTH_FLOW_SESSION_KEY, flowId), (Object)formData);
    }

    private void restoreFromSession() throws Exception {
        String sessionAttributeKey = String.format(OAUTH_FLOW_SESSION_KEY, this.getFlowId());
        Object formDataAttribute = this.getCurrentSession().getAttribute(sessionAttributeKey);
        if (formDataAttribute == null) {
            throw new Exception("No valid Data found in Session for OAuth Provider: " + this.getAuthorization());
        }
        FormData formData = (FormData)formDataAttribute;
        formData.restore(this);
        this.getCurrentSession().removeAttribute(sessionAttributeKey);
    }

    public abstract String buildRedirect(String var1);

    private String redirectUrl(String defaultUrl) {
        try {
            ServletActionContext.getResponse().sendRedirect(defaultUrl);
        }
        catch (IOException e) {
            log.error("Error sending redirect to: {}", (Object)defaultUrl);
        }
        return "none";
    }

    protected void validateJNDIScheme() {
        if (!this.getJndiName().toLowerCase().startsWith(JNDI_JAVA_SCHEME)) {
            this.addFieldError("jndiName", this.getText("AbstractSetupEmailAction.illegal.jndiname"));
        }
    }

    public static class FormData
    implements Serializable {
        private static final long serialVersionUID = -8142724891829457458L;
        private final Long id;
        private final String name;
        private final String toAddress;
        private final String hostname;
        private final String port;
        private final String protocol;
        private final String authorization;
        private final String username;

        public FormData(AbstractSetupEmailAction setupEmailAction) {
            this.id = setupEmailAction.getId();
            this.name = setupEmailAction.getName();
            this.toAddress = setupEmailAction.getEmailAddress();
            this.hostname = setupEmailAction.getHostname();
            this.port = setupEmailAction.getPort();
            this.protocol = setupEmailAction.getProtocol();
            this.authorization = setupEmailAction.getAuthorization();
            this.username = setupEmailAction.getUserName();
        }

        public void restore(AbstractSetupEmailAction setupEmailAction) {
            setupEmailAction.setId(this.id);
            setupEmailAction.setName(this.name);
            setupEmailAction.setEmailAddress(this.toAddress);
            setupEmailAction.setHostname(this.hostname);
            setupEmailAction.setPort(this.port);
            setupEmailAction.setProtocol(this.protocol);
            setupEmailAction.setAuthorization(this.authorization);
            setupEmailAction.setUserName(this.username);
        }
    }
}

