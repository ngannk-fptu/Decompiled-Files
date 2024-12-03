/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  com.atlassian.seraph.service.rememberme.RememberMeService
 *  com.google.common.annotations.VisibleForTesting
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.rememberme;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.plugins.authentication.impl.web.usercontext.rememberme.RememberMeCookieHandler;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.seraph.service.rememberme.RememberMeService;
import com.google.common.annotations.VisibleForTesting;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@JiraComponent
@ConfluenceComponent
@BambooComponent
public class SeraphRememberMeCookieHandler
implements RememberMeCookieHandler {
    @VisibleForTesting
    protected RememberMeService getRememberMeService() {
        return SecurityConfigFactory.getInstance().getRememberMeService();
    }

    @Override
    public void refreshRememberMeCookie(HttpServletRequest request, HttpServletResponse response, Principal username) {
        this.getRememberMeService().removeRememberMeCookie(request, response);
        this.getRememberMeService().addRememberMeCookie(request, response, username.getName());
    }
}

