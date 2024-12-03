/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.plugin.ConfluencePluginManager;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class MobileAppRequestFilter
extends AbstractHttpFilter {
    private static final String MOBILE_REQUEST_HEADER_NAME = "mobile-app-request";
    private static final String MOBILE_DISABLED_HEADER_NAME = "mobile-app-disabled";
    private static final String MOBILE_PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-mobile-plugin";
    private final Supplier<ConfluencePluginManager> pluginManagerSupplier = new LazyComponentReference("pluginManager");

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String mobileRequestHeader = request.getHeader(MOBILE_REQUEST_HEADER_NAME);
        if (StringUtils.isNotBlank((CharSequence)mobileRequestHeader) && Boolean.valueOf(mobileRequestHeader).booleanValue() && !this.getPluginManager().isPluginEnabled(MOBILE_PLUGIN_KEY)) {
            response.setHeader(MOBILE_DISABLED_HEADER_NAME, "true");
            response.sendError(503);
        } else {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
    }

    private ConfluencePluginManager getPluginManager() {
        return (ConfluencePluginManager)((Object)this.pluginManagerSupplier.get());
    }
}

