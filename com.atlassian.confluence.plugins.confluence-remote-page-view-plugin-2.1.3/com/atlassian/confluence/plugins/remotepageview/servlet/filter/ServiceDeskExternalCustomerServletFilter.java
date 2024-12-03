/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.access.AccessStatus
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.remotepageview.servlet.filter;

import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="rpv-serviceDeskExternalCustomerServletFilter-component")
public class ServiceDeskExternalCustomerServletFilter
extends AbstractHttpFilter {
    private final ConfluenceAccessManager confluenceAccessManager;

    @Autowired
    public ServiceDeskExternalCustomerServletFilter(@ComponentImport ConfluenceAccessManager confluenceAccessManager) {
        this.confluenceAccessManager = confluenceAccessManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        ConfluenceUser confluenceUser = AuthenticatedUserThreadLocal.get();
        AccessStatus userAccessStatus = this.confluenceAccessManager.getUserAccessStatus((User)confluenceUser);
        if (!userAccessStatus.canUseConfluence()) {
            try {
                AuthenticatedUserThreadLocal.reset();
                filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            }
            finally {
                AuthenticatedUserThreadLocal.set((ConfluenceUser)confluenceUser);
            }
        } else {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
    }
}

