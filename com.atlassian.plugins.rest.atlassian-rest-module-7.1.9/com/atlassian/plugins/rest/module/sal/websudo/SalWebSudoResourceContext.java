/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.plugins.rest.module.sal.websudo;

import com.atlassian.plugins.rest.common.sal.websudo.WebSudoResourceContext;
import com.atlassian.plugins.rest.module.servlet.ServletUtils;
import com.atlassian.sal.api.websudo.WebSudoManager;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SalWebSudoResourceContext
implements WebSudoResourceContext {
    private static final String BASIC_AUTHZ_TYPE_PREFIX = "Basic ";
    private static final String BEARER_AUTHZ_TYPE_PREFIX = "Bearer ";
    private final WebSudoManager webSudoManager;

    public SalWebSudoResourceContext(WebSudoManager webSudoManager) {
        this.webSudoManager = webSudoManager;
    }

    @Override
    public boolean shouldEnforceWebSudoProtection() {
        HttpServletRequest req = ServletUtils.getHttpServletRequest();
        if (null == req) {
            return false;
        }
        String authHeader = req.getHeader("Authorization");
        if (null != authHeader && (authHeader.startsWith(BASIC_AUTHZ_TYPE_PREFIX) || this.shouldDisableWebsudoForPersonalAccessTokens(req, authHeader))) {
            return false;
        }
        return !this.webSudoManager.canExecuteRequest(req);
    }

    private boolean shouldDisableWebsudoForPersonalAccessTokens(HttpServletRequest request, String authHeader) {
        HttpSession session = request.getSession(false);
        return Objects.nonNull(session) && authHeader.startsWith(BEARER_AUTHZ_TYPE_PREFIX) && Objects.nonNull(session.getAttribute("is.pats.enabled"));
    }
}

