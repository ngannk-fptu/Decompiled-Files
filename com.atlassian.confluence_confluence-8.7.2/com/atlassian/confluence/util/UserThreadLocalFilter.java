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

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
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

public class UserThreadLocalFilter
extends AbstractHttpFilter {
    protected Supplier<UserAccessor> userAccessor = new LazyComponentReference("userAccessor");

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        ConfluenceUser existingUser = null;
        try {
            if (GeneralUtil.isSetupComplete()) {
                ConfluenceUser user = this.getUserFromRequest(request);
                existingUser = AuthenticatedUserThreadLocal.get();
                AuthenticatedUserThreadLocal.set(user);
            }
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
        catch (Throwable throwable) {
            AuthenticatedUserThreadLocal.set(existingUser);
            throw throwable;
        }
        AuthenticatedUserThreadLocal.set(existingUser);
    }

    private ConfluenceUser getUserFromRequest(HttpServletRequest request) {
        String username = request.getRemoteUser();
        if (!StringUtils.isNotEmpty((CharSequence)username)) {
            return null;
        }
        return this.getUserAccessor().getUserByName(username);
    }

    protected UserAccessor getUserAccessor() {
        return (UserAccessor)this.userAccessor.get();
    }
}

