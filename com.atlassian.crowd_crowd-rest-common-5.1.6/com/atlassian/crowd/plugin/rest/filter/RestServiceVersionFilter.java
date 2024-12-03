/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.crowd.plugin.rest.filter;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.Validate;

@Named
public class RestServiceVersionFilter
implements Filter {
    private static final String EMBEDDED_CROWD_VERSION_NAME = "X-Embedded-Crowd-Version";
    private final ApplicationProperties applicationProperties;
    private final Map<String, String> httpHeaders = new HashMap<String, String>();
    private String applicationVersion;
    private String displayName;
    private String version;

    public RestServiceVersionFilter(String displayName, String version) {
        Validate.notNull((Object)displayName);
        Validate.notNull((Object)version);
        this.applicationProperties = null;
        this.displayName = displayName;
        this.version = version;
    }

    @Inject
    public RestServiceVersionFilter(@ComponentImport ApplicationProperties applicationProperties) {
        Validate.notNull((Object)applicationProperties);
        this.applicationProperties = applicationProperties;
        this.displayName = null;
        this.version = null;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.applicationVersion = String.format("%s/%s", this.getDisplayName(), this.getVersion());
        Enumeration names = filterConfig.getInitParameterNames();
        while (names.hasMoreElements()) {
            String headerName = (String)names.nextElement();
            this.httpHeaders.put(headerName, filterConfig.getInitParameter(headerName));
        }
    }

    public String getVersion() {
        if (this.version == null) {
            this.version = this.applicationProperties.getVersion();
        }
        return this.version;
    }

    public String getDisplayName() {
        if (this.displayName == null) {
            this.displayName = this.applicationProperties.getDisplayName();
        }
        return this.displayName;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        response.setHeader(EMBEDDED_CROWD_VERSION_NAME, this.applicationVersion);
        for (Map.Entry<String, String> entry : this.httpHeaders.entrySet()) {
            response.setHeader(entry.getKey(), entry.getValue());
        }
        chain.doFilter(servletRequest, (ServletResponse)response);
    }

    public void destroy() {
    }
}

