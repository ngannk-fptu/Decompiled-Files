/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.websudo.WebSudoManager
 *  com.atlassian.oauth.util.RequestAnnotations
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.google.common.base.Preconditions
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.sal.confluence.websudo;

import com.atlassian.oauth.util.RequestAnnotations;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.google.common.base.Preconditions;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ConfluenceWebSudoManager
implements WebSudoManager {
    private final com.atlassian.confluence.security.websudo.WebSudoManager internalWebSudoManager;

    public ConfluenceWebSudoManager(com.atlassian.confluence.security.websudo.WebSudoManager internalWebSudoManager) {
        this.internalWebSudoManager = (com.atlassian.confluence.security.websudo.WebSudoManager)Preconditions.checkNotNull((Object)internalWebSudoManager, (Object)"internalWebSudoManager");
    }

    public boolean canExecuteRequest(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        return !this.internalWebSudoManager.isEnabled() || this.isAccessTokenRequest(httpServletRequest) || this.internalWebSudoManager.hasValidSession(session);
    }

    private boolean isAccessTokenRequest(HttpServletRequest httpServletRequest) {
        return RequestAnnotations.isOAuthRequest((HttpServletRequest)httpServletRequest) || httpServletRequest.getAttribute("access.token.request") != null;
    }

    public void enforceWebSudoProtection(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect(this.internalWebSudoManager.buildAuthenticationRedirectUri(request).toString());
        }
        catch (IOException e) {
            throw new IllegalStateException("Failed to redirect to /authenticate.action");
        }
    }

    public void willExecuteWebSudoRequest(HttpServletRequest httpServletRequest) throws WebSudoSessionException {
        if (!this.canExecuteRequest(httpServletRequest)) {
            throw new WebSudoSessionException("Invalid request: Not in a WebSudo session");
        }
        this.internalWebSudoManager.markWebSudoRequest(httpServletRequest);
    }
}

