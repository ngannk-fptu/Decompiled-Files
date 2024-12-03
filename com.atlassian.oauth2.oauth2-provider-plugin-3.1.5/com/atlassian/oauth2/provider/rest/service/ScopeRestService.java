/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.rest.service;

import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScopeRestService {
    private static final Logger logger = LoggerFactory.getLogger(ScopeRestService.class);
    private final ScopeResolver scopeResolver;

    public ScopeRestService(ScopeResolver scopeResolver) {
        this.scopeResolver = scopeResolver;
    }

    public Scope getOrClientDefault(String scope, Scope clientScope) {
        if (StringUtils.isBlank((CharSequence)scope)) {
            logger.debug("No scope provided, defaulting to [{}] scope", (Object)clientScope);
            return clientScope;
        }
        return this.scopeResolver.getScope(scope);
    }
}

