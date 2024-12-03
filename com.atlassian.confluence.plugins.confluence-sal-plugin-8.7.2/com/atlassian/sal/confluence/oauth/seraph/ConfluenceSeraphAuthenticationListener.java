/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.security.LoginDetails
 *  com.atlassian.confluence.event.events.security.LoginDetails$LoginSource
 *  com.atlassian.confluence.event.events.security.LoginEvent
 *  com.atlassian.confluence.security.login.LoginManager
 *  com.atlassian.confluence.security.seraph.ConfluenceUserPrincipal
 *  com.atlassian.confluence.user.LoginDetailsHelper
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.auth.Authenticator$Result
 *  com.atlassian.sal.api.auth.Authenticator$Result$Success
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.core.auth.SeraphAuthenticationListener
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.sal.confluence.oauth.seraph;

import com.atlassian.confluence.event.events.security.LoginDetails;
import com.atlassian.confluence.event.events.security.LoginEvent;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.security.seraph.ConfluenceUserPrincipal;
import com.atlassian.confluence.user.LoginDetailsHelper;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.auth.Authenticator;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.core.auth.SeraphAuthenticationListener;
import java.io.Serializable;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConfluenceSeraphAuthenticationListener
extends SeraphAuthenticationListener {
    private final EventPublisher eventPublisher;
    private final LoginManager loginManager;

    public ConfluenceSeraphAuthenticationListener(EventPublisher eventPublisher, LoginManager loginManager) {
        this.eventPublisher = eventPublisher;
        this.loginManager = loginManager;
    }

    public void authenticationSuccess(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response) {
        ConfluenceUserPrincipal principal = ConfluenceUserPrincipal.of((Principal)result.getPrincipal());
        super.authenticationSuccess((Authenticator.Result)new Authenticator.Result.Success(new Message(){

            public String getKey() {
                return "Successful authentication and conversion to ConfluenceUserPrincipal";
            }

            public Serializable[] getArguments() {
                return null;
            }
        }, (Principal)principal), request, response);
        this.postLogin(request, principal);
    }

    private void postLogin(HttpServletRequest request, ConfluenceUserPrincipal principal) {
        if (LoginDetailsHelper.isSsoLogin((HttpServletRequest)request) && principal != null) {
            this.loginManager.onSuccessfulLoginAttempt(principal.getName(), request);
            LoginDetails loginDetails = new LoginDetails(LoginDetails.LoginSource.SSO, null);
            this.eventPublisher.publish((Object)new LoginEvent((Object)this, principal.getName(), request.getSession().getId(), request.getRemoteHost(), request.getRemoteAddr(), loginDetails));
        }
    }
}

