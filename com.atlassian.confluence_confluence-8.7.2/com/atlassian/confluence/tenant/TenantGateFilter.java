/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.google.common.base.Throwables
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.BooleanUtils
 */
package com.atlassian.confluence.tenant;

import com.atlassian.confluence.tenant.TenantGate;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.google.common.base.Throwables;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;

@Deprecated(forRemoval=true)
public class TenantGateFilter
extends AbstractHttpFilter {
    private boolean permit;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.permit = BooleanUtils.toBoolean((String)filterConfig.getInitParameter("permit"));
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            TenantGate.permit(this.permit, () -> {
                filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
                return null;
            }).call();
        }
        catch (Exception e) {
            Throwables.throwIfInstanceOf((Throwable)e, IOException.class);
            Throwables.throwIfInstanceOf((Throwable)e, ServletException.class);
            Throwables.throwIfInstanceOf((Throwable)e, RuntimeException.class);
            throw new RuntimeException(e);
        }
    }
}

