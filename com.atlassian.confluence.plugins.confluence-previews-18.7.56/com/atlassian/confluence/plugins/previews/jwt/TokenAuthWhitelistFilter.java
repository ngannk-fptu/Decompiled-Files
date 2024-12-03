/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.previews.jwt;

import com.atlassian.confluence.plugins.previews.jwt.JwtTokenService;
import com.atlassian.core.filters.AbstractHttpFilter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenAuthWhitelistFilter
extends AbstractHttpFilter {
    private static final Logger log = LoggerFactory.getLogger(TokenAuthWhitelistFilter.class);
    private JwtTokenService jwtTokenService;

    @Autowired
    public TokenAuthWhitelistFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (this.jwtTokenService.isSignedByPreviewsPlugin(request)) {
            this.whitelistRequest(request);
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    protected void whitelistRequest(HttpServletRequest request) {
        log.info("TokenAuthWhitelistFilter: whitelisting request {}", (Object)TokenAuthWhitelistFilter.sanitizeStr(request.getRequestURI()));
        request.setAttribute("TokenAuthWhitelistOK", (Object)Boolean.TRUE);
    }

    private static String sanitizeStr(String str) {
        return str.replaceAll("[\r\n]", "");
    }
}

