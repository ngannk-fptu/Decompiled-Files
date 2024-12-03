/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.actions.AbstractSpaceAction
 *  com.atlassian.confluence.util.HTMLPairType
 *  com.atlassian.confluence.util.breadcrumbs.Breadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.opensymphony.xwork2.Action
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.archive.actions;

import com.atlassian.confluence.mail.archive.AbstractMailAccount;
import com.atlassian.confluence.mail.archive.ImapMailAccount;
import com.atlassian.confluence.mail.archive.MailAccount;
import com.atlassian.confluence.mail.archive.MailAccountManager;
import com.atlassian.confluence.mail.archive.MailPollResult;
import com.atlassian.confluence.mail.archive.PopMailAccount;
import com.atlassian.confluence.mail.archive.actions.MailActionBreadcrumb;
import com.atlassian.confluence.mail.archive.oauth.OAuthManager;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.util.HTMLPairType;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.opensymphony.xwork2.Action;
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

public abstract class AbstractMailAccountAction
extends AbstractSpaceAction
implements BreadcrumbAware {
    private static final Logger log = LoggerFactory.getLogger(AbstractMailAccountAction.class);
    private static final String OAUTH_FLOW_SESSION_KEY = "oauth-flow-%s";
    protected MailAccountManager mailAccountManager;
    protected OAuthManager oAuthManager;
    protected String name;
    protected String description;
    protected String hostname;
    protected String protocol;
    protected String authentication;
    protected String username;
    protected String password;
    protected int port;
    protected String flowId;
    protected String token;
    protected boolean secure;
    protected String confirm;
    protected String testConnection;
    protected MailPollResult pollResult;
    private BreadcrumbGenerator breadcrumbGenerator;
    protected ApplicationProperties applicationProperties;

    public void validate() {
        super.validate();
        if (StringUtils.isBlank((CharSequence)this.getName())) {
            this.addFieldError("name", this.getText("error.account.name.reqd"));
        }
        if (StringUtils.isBlank((CharSequence)this.getHostname())) {
            this.addFieldError("hostname", this.getText("error.hostname.reqd"));
        }
        if (StringUtils.isBlank((CharSequence)this.getUsername())) {
            this.addFieldError("username", this.getText("error.username.reqd"));
        }
        if ("BasicAuthentication".equals(this.getAuthentication()) && StringUtils.isBlank((CharSequence)this.getPassword())) {
            this.addFieldError("password", this.getText("error.password.reqd"));
        }
        if (this.getSpace().isPersonal()) {
            this.addActionError(this.getText("error.personal.space"));
        }
    }

    public String doDefault() throws Exception {
        if (this.getFlowId() != null) {
            this.restoreFromSession();
            this.setToken(this.oAuthManager.completeOAuthFlow(this.getCurrentSession(), this.getAuthentication()));
            this.pollResult = MailPollResult.success("Successfully generated an OAuth token", 0);
        }
        return super.doDefault();
    }

    public String execute() throws Exception {
        MailAccount mailAccount = this.createMailAccountFromFormData();
        if (this.getTestConnection() != null) {
            HttpSession currentSession = this.getCurrentSession();
            String selectedAuthentication = this.getAuthentication();
            if (selectedAuthentication != null && !"BasicAuthentication".equals(selectedAuthentication)) {
                OAuthManager.OAuthResult oauthResult = this.oAuthManager.initialiseOAuthFlow(currentSession, selectedAuthentication, this::buildRedirect);
                this.storeSessionAttributes(currentSession, oauthResult.getFlowId());
                return this.redirectUrl(oauthResult.getRedirectUrl());
            }
            this.pollResult = this.mailAccountManager.updateAccountStatus(mailAccount);
            return "testconnection";
        }
        return this.executeInternal();
    }

    protected abstract String buildRedirect(String var1);

    abstract String executeInternal() throws Exception;

    protected List<String> getPermissionTypes() {
        List permissions = super.getPermissionTypes();
        this.addPermissionTypeTo("SETSPACEPERMISSIONS", permissions);
        return permissions;
    }

    public void setMailAccountManager(MailAccountManager mailAccountManager) {
        this.mailAccountManager = mailAccountManager;
    }

    public abstract boolean isEditAction();

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHostname() {
        return this.hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAuthentication() {
        return this.authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isSecure() {
        return this.secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public String getConfirm() {
        return this.confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getTestConnection() {
        return this.testConnection;
    }

    public void setTestConnection(String testConnection) {
        this.testConnection = testConnection;
    }

    public MailPollResult getPollResult() {
        return this.pollResult;
    }

    public void setPollResult(MailPollResult pollResult) {
        this.pollResult = pollResult;
    }

    public String getFlowId() {
        return this.flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setBreadcrumbGenerator(@ComponentImport BreadcrumbGenerator breadcrumbGenerator) {
        this.breadcrumbGenerator = breadcrumbGenerator;
    }

    public void setApplicationProperties(@ComponentImport ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public void setoAuthManager(OAuthManager oAuthManager) {
        this.oAuthManager = oAuthManager;
    }

    protected MailAccount createMailAccountFromFormData() {
        AbstractMailAccount mailAccount;
        if (this.isProtocol("pop3")) {
            mailAccount = new PopMailAccount(0, this.name, this.description, this.hostname, this.username, this.password, this.port, this.isProtocolSecure(), this.authentication);
        } else if (this.isProtocol("imap")) {
            mailAccount = new ImapMailAccount(0, this.name, this.description, this.hostname, this.username, this.password, this.port, this.isProtocolSecure(), this.authentication);
        } else {
            throw new UnsupportedOperationException("Protocol: " + this.getProtocol() + " not supported.");
        }
        return mailAccount;
    }

    private boolean isProtocolSecure() {
        return this.getProtocol().endsWith("s");
    }

    protected boolean isProtocol(String protocol) {
        return this.getProtocol().contains(protocol);
    }

    public Breadcrumb getBreadcrumb() {
        return new MailActionBreadcrumb((Object)this, this.getSpace(), null, this.breadcrumbGenerator.getSpaceAdminBreadcrumb((Action)this, this.getSpace()));
    }

    public List<HTMLPairType> getAuthenticationList() {
        ArrayList<HTMLPairType> result = new ArrayList<HTMLPairType>();
        result.add(new HTMLPairType("BasicAuthentication", this.getText("basic.authentication")));
        result.addAll(this.getOAuth2Configurations());
        return result;
    }

    private List<HTMLPairType> getOAuth2Configurations() {
        return this.oAuthManager.getConfiguredOAuthProvider().stream().map(entity -> new HTMLPairType(entity.getId(), entity.toString())).collect(Collectors.toList());
    }

    private String redirectUrl(String defaultUrl) {
        try {
            ServletActionContext.getResponse().sendRedirect(defaultUrl);
        }
        catch (IOException e) {
            log.error("Error sending redirect to: {}", (Object)defaultUrl);
        }
        return "none";
    }

    protected void restoreFromSession() throws Exception {
        String sessionAttributeKey = String.format(OAUTH_FLOW_SESSION_KEY, this.getFlowId());
        Object formDataAttribute = this.getCurrentSession().getAttribute(sessionAttributeKey);
        if (formDataAttribute == null) {
            throw new Exception("No valid Data found in Session for OAuth Provider: " + this.getFlowId());
        }
        FormData formData = (FormData)formDataAttribute;
        formData.restore(this);
        this.getCurrentSession().removeAttribute(sessionAttributeKey);
    }

    protected void storeSessionAttributes(HttpSession currentSession, String flowId) {
        FormData formData = new FormData(this);
        currentSession.setAttribute(String.format(OAUTH_FLOW_SESSION_KEY, flowId), (Object)formData);
    }

    static class FormData
    implements Serializable {
        static final long serialVersionUID = -6081351043965272464L;
        private final String name;
        private final String description;
        private final String hostname;
        private final String username;
        private final String protocol;
        private final int port;
        private final String authentication;

        public FormData(AbstractMailAccountAction accountAction) {
            this.name = accountAction.getName();
            this.description = accountAction.getDescription();
            this.hostname = accountAction.getHostname();
            this.username = accountAction.getUsername();
            this.protocol = accountAction.getProtocol();
            this.port = accountAction.getPort();
            this.authentication = accountAction.getAuthentication();
        }

        public void restore(AbstractMailAccountAction accountAction) {
            accountAction.setName(this.name);
            accountAction.setDescription(this.description);
            accountAction.setHostname(this.hostname);
            accountAction.setUsername(this.username);
            accountAction.setProtocol(this.protocol);
            accountAction.setPort(this.port);
            accountAction.setAuthentication(this.authentication);
        }
    }
}

