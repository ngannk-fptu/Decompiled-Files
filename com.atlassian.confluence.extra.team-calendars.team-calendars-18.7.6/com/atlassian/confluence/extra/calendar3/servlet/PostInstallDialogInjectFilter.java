/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 */
package com.atlassian.confluence.extra.calendar3.servlet;

import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.plugin.webresource.WebResourceManager;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.commons.lang.BooleanUtils;

public class PostInstallDialogInjectFilter
implements Filter {
    private final WebResourceManager webResourceManager;
    private final BuildInformationManager buildInformationManager;
    private String postInstallDialogResourceKey;
    private String upmLicensePromptResourcesKey;

    public PostInstallDialogInjectFilter(WebResourceManager webResourceManager, BuildInformationManager buildInformationManager) {
        this.webResourceManager = webResourceManager;
        this.buildInformationManager = buildInformationManager;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.postInstallDialogResourceKey = this.buildInformationManager.getPluginKey() + ":post-install-dialog-resources";
        this.upmLicensePromptResourcesKey = this.buildInformationManager.getPluginKey() + ":upm-license-prompt-resources";
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        this.webResourceManager.requireResource(this.postInstallDialogResourceKey);
        if (BooleanUtils.toBoolean(servletRequest.getParameter("unlicensed"))) {
            this.webResourceManager.requireResource(this.upmLicensePromptResourcesKey);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
    }
}

