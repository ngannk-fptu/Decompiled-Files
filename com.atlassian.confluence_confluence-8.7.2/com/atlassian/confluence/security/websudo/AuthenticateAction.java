/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.security.websudo;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.event.events.internal.auth.SudoAuthFailEvent;
import com.atlassian.confluence.event.events.internal.auth.SudoAuthSuccessEvent;
import com.atlassian.confluence.security.websudo.WebSudoManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.base.Preconditions;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

public class AuthenticateAction
extends ConfluenceActionSupport {
    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    private String password;
    private String destination;
    private WebSudoManager webSudoManager;
    private EventPublisher eventPublisher;

    @Override
    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doDefault() throws Exception {
        this.password = null;
        return super.doDefault();
    }

    public String execute() throws Exception {
        if (StringUtils.isBlank((CharSequence)this.password)) {
            this.addFieldError("password", this.getText("websudo.password.empty"));
            SudoAuthFailEvent sudoAuthFailEvent = new SudoAuthFailEvent(this);
            this.eventPublisher.publish((Object)sudoAuthFailEvent);
            return "input";
        }
        if (this.userAccessor.authenticate(this.getUsername(), this.password)) {
            this.webSudoManager.startSession(ServletActionContext.getRequest(), ServletActionContext.getResponse());
            SudoAuthSuccessEvent sudoAuthSuccessEvent = new SudoAuthSuccessEvent(this);
            this.eventPublisher.publish((Object)sudoAuthSuccessEvent);
            try {
                if (StringUtils.isBlank((CharSequence)this.destination) || !this.isDestinationAllowed(this.destination)) {
                    this.destination = null;
                }
            }
            catch (URISyntaxException e) {
                this.destination = null;
            }
            return "success";
        }
        this.addFieldError("password", this.getText("websudo.password.wrong"));
        SudoAuthFailEvent sudoAuthFailEvent = new SudoAuthFailEvent(this);
        this.eventPublisher.publish((Object)sudoAuthFailEvent);
        return "input";
    }

    public User getUser() {
        return this.getUserByName(this.getUsername());
    }

    public String getUsername() {
        return this.getAuthenticatedUser().getName();
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setWebSudoManager(WebSudoManager webSudoManager) {
        this.webSudoManager = (WebSudoManager)Preconditions.checkNotNull((Object)webSudoManager);
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = (EventPublisher)Preconditions.checkNotNull((Object)eventPublisher);
    }

    private boolean isDestinationAllowed(String destination) throws URISyntaxException {
        if (StringUtils.isEmpty((CharSequence)destination) || destination.startsWith("//")) {
            return false;
        }
        URI uri = this.toURI(destination);
        String scheme = uri.getScheme();
        if (StringUtils.isNotBlank((CharSequence)scheme) && !AuthenticateAction.isValidScheme(scheme)) {
            return false;
        }
        URI baseURI = new URI(this.getGlobalSettings().getBaseUrl());
        URI resolved = baseURI.resolve(destination);
        if (StringUtils.isBlank((CharSequence)resolved.getScheme())) {
            return false;
        }
        return baseURI.getHost().equals(resolved.getHost());
    }

    public static boolean isValidScheme(String scheme) {
        return HTTP.equalsIgnoreCase(scheme) || HTTPS.equalsIgnoreCase(scheme);
    }

    private URI toURI(String url) throws URISyntaxException {
        url = url.replaceAll("//{2,}", "//");
        return new URI(url);
    }
}

