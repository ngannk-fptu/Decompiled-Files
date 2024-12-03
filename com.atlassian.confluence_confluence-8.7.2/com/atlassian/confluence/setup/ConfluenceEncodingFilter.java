/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.encoding.FixedHtmlEncodingResponseWrapper
 *  com.atlassian.johnson.Johnson
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.setup;

import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.web.filter.AbstractStaticResourceAwareFilter;
import com.atlassian.core.filters.encoding.FixedHtmlEncodingResponseWrapper;
import com.atlassian.johnson.Johnson;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ConfluenceEncodingFilter
extends AbstractStaticResourceAwareFilter {
    private Supplier<SettingsManager> settingsManagerRef = new LazyComponentReference("settingsManager");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        request.setCharacterEncoding(this.getEncodingInternal());
        response.setContentType("text/html; charset=" + this.getEncodingInternal());
        filterChain.doFilter((ServletRequest)request, (ServletResponse)new FixedHtmlEncodingResponseWrapper(response));
    }

    private String getEncodingInternal() {
        if (!GeneralUtil.isSetupComplete() || !ContainerManager.isContainerSetup() || Johnson.getEventContainer().hasEvents()) {
            return "UTF-8";
        }
        return this.getGlobalDefaultEncoding();
    }

    private @NonNull String getGlobalDefaultEncoding() {
        return Optional.ofNullable((SettingsManager)this.settingsManagerRef.get()).map(SettingsManager::getGlobalSettings).map(Settings::getDefaultEncoding).filter(StringUtils::isNotBlank).orElse("UTF-8");
    }
}

