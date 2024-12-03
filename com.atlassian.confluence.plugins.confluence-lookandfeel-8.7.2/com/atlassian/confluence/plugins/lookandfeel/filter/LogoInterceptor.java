/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.plugins.lookandfeel.filter;

import com.atlassian.confluence.plugins.lookandfeel.SiteLogo;
import com.atlassian.confluence.plugins.lookandfeel.SiteLogoManager;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class LogoInterceptor
implements Filter {
    private static final String CONFLUENCE_LOGO = "confluence-logo.png";
    private FilterConfig filterConfig;
    private final SiteLogoManager siteLogoManager;

    public LogoInterceptor(SiteLogoManager siteLogoManager) {
        this.siteLogoManager = siteLogoManager;
    }

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        SiteLogo siteLogo;
        HttpServletRequest req;
        String requestURL;
        if (request instanceof HttpServletRequest && (requestURL = (req = (HttpServletRequest)request).getRequestURL().toString()).endsWith(CONFLUENCE_LOGO) && this.siteLogoManager.useCustomLogo() && (siteLogo = this.siteLogoManager.getCurrent()) != null) {
            this.filterConfig.getServletContext().getRequestDispatcher(siteLogo.getDownloadPath()).forward(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    public void destroy() {
    }
}

