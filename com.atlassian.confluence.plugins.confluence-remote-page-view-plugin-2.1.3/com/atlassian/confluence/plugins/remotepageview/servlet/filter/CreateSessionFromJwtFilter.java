/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.remotepageview.servlet.filter;

import com.atlassian.confluence.plugins.remotepageview.api.service.TokenService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="rpv-new-session-from-jwt-filter")
@ExportAsService
public class CreateSessionFromJwtFilter
extends AbstractHttpFilter {
    private final TokenService tokenService;

    @Autowired
    public CreateSessionFromJwtFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    protected void doFilter(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        Optional<ConfluenceUser> userFromJwtTokenOpt;
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null && (userFromJwtTokenOpt = this.tokenService.getUserFromRequest((ServletRequest)httpServletRequest)).isPresent()) {
            AuthenticatedUserThreadLocal.set((ConfluenceUser)userFromJwtTokenOpt.get());
            HttpSession httpSession = httpServletRequest.getSession();
            httpSession.setAttribute("seraph_defaultauthenticator_user", (Object)userFromJwtTokenOpt.get());
            httpSession.setAttribute("seraph_defaultauthenticator_logged_out_user", null);
        }
        filterChain.doFilter((ServletRequest)httpServletRequest, (ServletResponse)httpServletResponse);
    }
}

