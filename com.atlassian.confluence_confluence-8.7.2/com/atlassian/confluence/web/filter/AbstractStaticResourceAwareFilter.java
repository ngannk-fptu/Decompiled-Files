/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.google.common.collect.ImmutableSet
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.web.filter;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractStaticResourceAwareFilter
extends AbstractHttpFilter {
    @VisibleForTesting
    public static final Set<String> staticSuffixes = ImmutableSet.of((Object)".css", (Object)".js", (Object)".png", (Object)".jpg", (Object)".jpeg", (Object)".gif", (Object[])new String[]{".ico", ".svg"});

    protected final void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!this.isStaticResource(request.getPathInfo())) {
            this.doFilterInternal(request, response, filterChain);
        } else {
            String settingEncoding = GeneralUtil.getCharacterEncoding();
            if (response.getCharacterEncoding() != null && !settingEncoding.equals(response.getCharacterEncoding())) {
                response.setCharacterEncoding(settingEncoding);
            }
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
    }

    protected abstract void doFilterInternal(HttpServletRequest var1, HttpServletResponse var2, FilterChain var3) throws IOException, ServletException;

    private boolean isStaticResource(String path) {
        return path != null && staticSuffixes.stream().anyMatch(suffix -> StringUtils.endsWithIgnoreCase((CharSequence)path, (CharSequence)suffix));
    }
}

