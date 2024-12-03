/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugins.authentication.impl.web.filter.authentication;

import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.LoginOptionsService;
import com.atlassian.plugins.authentication.impl.johnson.JohnsonChecker;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandlerProvider;
import com.atlassian.plugins.authentication.impl.web.filter.authentication.AuthenticationFilter;
import com.google.common.base.Strings;
import javax.servlet.http.HttpServletRequest;

public class SeraphAuthenticationFilter
extends AuthenticationFilter {
    public static final String SERAPH_DESTINATION_QUERY_PARAM = "os_destination";

    public SeraphAuthenticationFilter(AuthenticationHandlerProvider authenticationHandlerProvider, IdpConfigService idpConfigService, LoginOptionsService loginOptionsService, JohnsonChecker johnsonChecker) {
        super(authenticationHandlerProvider, idpConfigService, loginOptionsService, johnsonChecker);
    }

    @Override
    protected String extractRequestedUrl(HttpServletRequest req) {
        return Strings.emptyToNull((String)req.getParameter(SERAPH_DESTINATION_QUERY_PARAM));
    }
}

