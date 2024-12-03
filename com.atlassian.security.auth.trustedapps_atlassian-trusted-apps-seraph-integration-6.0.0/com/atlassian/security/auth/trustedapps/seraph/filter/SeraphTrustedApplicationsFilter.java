/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsManager
 *  com.atlassian.security.auth.trustedapps.UserResolver
 *  com.atlassian.security.auth.trustedapps.filter.AuthenticationController
 *  com.atlassian.security.auth.trustedapps.filter.AuthenticationListener
 *  com.atlassian.security.auth.trustedapps.filter.TrustedApplicationsFilter
 *  com.atlassian.seraph.auth.RoleMapper
 *  com.atlassian.seraph.config.SecurityConfigFactory
 */
package com.atlassian.security.auth.trustedapps.seraph.filter;

import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;
import com.atlassian.security.auth.trustedapps.UserResolver;
import com.atlassian.security.auth.trustedapps.filter.AuthenticationController;
import com.atlassian.security.auth.trustedapps.filter.AuthenticationListener;
import com.atlassian.security.auth.trustedapps.filter.TrustedApplicationsFilter;
import com.atlassian.security.auth.trustedapps.seraph.filter.SeraphAuthenticationController;
import com.atlassian.security.auth.trustedapps.seraph.filter.SeraphAuthenticationListener;
import com.atlassian.seraph.auth.RoleMapper;
import com.atlassian.seraph.config.SecurityConfigFactory;

public class SeraphTrustedApplicationsFilter
extends TrustedApplicationsFilter {
    public SeraphTrustedApplicationsFilter(TrustedApplicationsManager appManager, UserResolver resolver) {
        this(appManager, resolver, SecurityConfigFactory.getInstance().getRoleMapper());
    }

    protected SeraphTrustedApplicationsFilter(TrustedApplicationsManager appManager, UserResolver resolver, RoleMapper roleMapper) {
        super(appManager, resolver, (AuthenticationController)new SeraphAuthenticationController(roleMapper), (AuthenticationListener)new SeraphAuthenticationListener());
    }
}

