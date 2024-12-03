/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.MethodInvocation
 *  com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.ws.rs.core.MultivaluedMap
 */
package com.atlassian.applinks.core.rest.auth;

import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import com.atlassian.sal.api.message.I18nResolver;
import java.lang.reflect.InvocationTargetException;
import javax.ws.rs.core.MultivaluedMap;

public class AdminApplicationLinksInterceptor
implements ResourceInterceptor {
    private final AdminUIAuthenticator authenticator;
    private final I18nResolver i18nResolver;

    public AdminApplicationLinksInterceptor(AdminUIAuthenticator authenticator, I18nResolver i18nResolver) {
        this.authenticator = authenticator;
        this.i18nResolver = i18nResolver;
    }

    public void intercept(MethodInvocation invocation) throws IllegalAccessException, InvocationTargetException {
        MultivaluedMap params = invocation.getHttpContext().getRequest().getQueryParameters();
        if (this.authenticator.checkAdminUIAccessByPasswordOrCurrentUser((String)params.getFirst((Object)"al_username"), (String)params.getFirst((Object)"al_password"))) {
            invocation.invoke();
        } else {
            invocation.getHttpContext().getResponse().setResponse(RestUtil.unauthorized(this.i18nResolver.getText("applinks.error.only.admin")));
        }
    }
}

