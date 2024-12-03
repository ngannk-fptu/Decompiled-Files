/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.webdav.servlet.filter;

import com.atlassian.confluence.extra.webdav.WebdavSettingsManager;
import com.atlassian.confluence.extra.webdav.servlet.filter.AbstractHttpFilter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;

public class ClientWriteDenyFilter
extends AbstractHttpFilter {
    private final WebdavSettingsManager webdavSettingsManager;

    public ClientWriteDenyFilter(WebdavSettingsManager webdavSettingsManager) {
        this.webdavSettingsManager = webdavSettingsManager;
    }

    private boolean isWebdavClientDenied(String userAgent, String method) {
        return (method.equalsIgnoreCase("PUT") || method.equalsIgnoreCase("COPY") || method.equalsIgnoreCase("DELETE") || method.equalsIgnoreCase("MKCOL") || method.equalsIgnoreCase("MOVE")) && this.webdavSettingsManager.isClientInWriteBlacklist(userAgent);
    }

    @Override
    public void doFilter(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (this.isWebdavClientDenied(StringUtils.defaultString((String)httpServletRequest.getHeader("User-Agent")), httpServletRequest.getMethod())) {
            httpServletResponse.setStatus(403);
        } else {
            filterChain.doFilter((ServletRequest)httpServletRequest, (ServletResponse)httpServletResponse);
        }
    }
}

