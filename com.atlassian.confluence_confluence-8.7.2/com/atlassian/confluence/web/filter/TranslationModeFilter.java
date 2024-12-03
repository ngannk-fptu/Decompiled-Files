/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.spring.container.ContainerManager
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.web.filter;

import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.DefaultI18nModeManager;
import com.atlassian.confluence.util.i18n.TranslationMode;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.spring.container.ContainerManager;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TranslationModeFilter
extends AbstractHttpFilter {
    protected DefaultI18nModeManager i18nModeManager;

    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String translationModeParam = request.getParameter("i18ntranslate");
        DefaultI18nModeManager modeManager = this.getI18nModeManager();
        TranslationMode translationMode = null;
        if (modeManager != null) {
            translationMode = modeManager.getModeForString(translationModeParam);
        }
        if (translationMode != null) {
            modeManager.setTranslationMode(request, translationMode);
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    public DefaultI18nModeManager getI18nModeManager() {
        if (this.i18nModeManager == null && GeneralUtil.isSetupComplete() && ContainerManager.isContainerSetup()) {
            this.i18nModeManager = (DefaultI18nModeManager)ContainerManager.getComponent((String)"translationModeManager");
        }
        return this.i18nModeManager;
    }
}

