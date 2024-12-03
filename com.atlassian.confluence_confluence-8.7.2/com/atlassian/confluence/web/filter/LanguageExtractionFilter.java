/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.web.filter;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LanguageExtractionFilter
extends AbstractHttpFilter {
    private Supplier<LocaleManager> localeManager = new LazyComponentReference("localeManager");

    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Optional<String> languageHeader = Optional.ofNullable(request.getHeader("accept-language"));
        Optional<String> languageCookie = Optional.ofNullable(GeneralUtil.getCookieValue(request, "confluence-language"));
        languageCookie.ifPresent(cookieValue -> this.getLocaleManager().ifPresent(localeManager -> localeManager.setLanguage((String)cookieValue)));
        languageHeader.ifPresent(headerValue -> this.getLocaleManager().ifPresent(localeManager -> localeManager.setRequestLanguages((String)headerValue)));
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    private Optional<LocaleManager> getLocaleManager() {
        if (!GeneralUtil.isSetupComplete() || !ContainerManager.isContainerSetup()) {
            return Optional.empty();
        }
        return Optional.ofNullable((LocaleManager)this.localeManager.get());
    }
}

