/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.webdav.servlet.filter;

import com.atlassian.confluence.extra.webdav.servlet.filter.AbstractPrefixAwareFilter;
import com.atlassian.confluence.extra.webdav.util.UserAgentUtil;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebdavRequestForwardFilter
extends AbstractPrefixAwareFilter {
    private static final Logger logger = LoggerFactory.getLogger(WebdavRequestForwardFilter.class);
    private String mountPointPrefix;
    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.filterConfig = filterConfig;
        this.mountPointPrefix = StringUtils.defaultString((String)filterConfig.getInitParameter("mount-point-prefix"));
    }

    @Override
    protected boolean handles(HttpServletRequest request, HttpServletResponse response) {
        String userAgent = request.getHeader("User-Agent");
        return !UserAgentUtil.isOsxFinder(userAgent) && StringUtils.isEmpty((String)request.getContextPath());
    }

    @Override
    public void doFilter(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        String originalUri = httpServletRequest.getRequestURI();
        if (StringUtils.startsWith((String)originalUri, (String)this.mountPointPrefix)) {
            String targetPath = this.getPrefix() + originalUri.substring(this.mountPointPrefix.length());
            logger.debug(String.format("Forwarding webdav request to WebDavServlet on path :%s", targetPath));
            this.filterConfig.getServletContext().getRequestDispatcher(targetPath).forward((ServletRequest)httpServletRequest, (ServletResponse)httpServletResponse);
            return;
        }
        if (StringUtils.equalsIgnoreCase((String)httpServletRequest.getMethod(), (String)"OPTIONS") && StringUtils.equals((String)"/", (String)originalUri)) {
            httpServletResponse.addHeader("MS-Author-Via", "DAV");
            httpServletResponse.setStatus(200);
            return;
        }
        filterChain.doFilter((ServletRequest)httpServletRequest, (ServletResponse)httpServletResponse);
    }
}

