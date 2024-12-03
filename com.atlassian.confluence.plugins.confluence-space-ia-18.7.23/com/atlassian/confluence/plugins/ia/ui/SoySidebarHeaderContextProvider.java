/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.sal.api.web.context.HttpContext
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.ia.ui;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.sal.api.web.context.HttpContext;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class SoySidebarHeaderContextProvider
implements ContextProvider {
    private static final String SIDEBAR_WIDTH_KEY = "confluence-sidebar.width";
    private static final String DEFAULT_SIDEBAR_WIDTH = "285";
    private final HttpContext httpContext;

    public SoySidebarHeaderContextProvider(@ComponentImport HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        HttpServletRequest request = this.httpContext.getRequest();
        String sidebarWidth = DEFAULT_SIDEBAR_WIDTH;
        if (request != null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (!cookie.getName().equals(SIDEBAR_WIDTH_KEY) || !StringUtils.isNumeric((CharSequence)cookie.getValue())) continue;
                sidebarWidth = cookie.getValue();
                break;
            }
        }
        context.put("sidebarWidth", sidebarWidth);
        return context;
    }
}

