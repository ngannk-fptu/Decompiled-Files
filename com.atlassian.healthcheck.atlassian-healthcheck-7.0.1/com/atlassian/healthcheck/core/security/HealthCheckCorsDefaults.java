/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.CorsHeaders
 *  com.atlassian.plugins.rest.common.security.descriptor.CorsDefaults
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.healthcheck.core.security;

import com.atlassian.plugins.rest.common.security.CorsHeaders;
import com.atlassian.plugins.rest.common.security.descriptor.CorsDefaults;
import com.google.common.collect.ImmutableSet;
import java.net.URI;
import java.util.Set;

public class HealthCheckCorsDefaults
implements CorsDefaults {
    private static final String[] ALLOWED_ORIGINS = new String[]{".atlassian.com"};

    public boolean allowsCredentials(String origin) throws IllegalArgumentException {
        return false;
    }

    public boolean allowsOrigin(String origin) throws IllegalArgumentException {
        URI normalizedOrigin = URI.create(origin).normalize();
        String originHost = normalizedOrigin.getHost();
        if (originHost == null) {
            return false;
        }
        for (String allowedOrigin : ALLOWED_ORIGINS) {
            if (!originHost.endsWith(allowedOrigin)) continue;
            return true;
        }
        return false;
    }

    public Set<String> getAllowedRequestHeaders(String origin) throws IllegalArgumentException {
        return ImmutableSet.of((Object)CorsHeaders.ORIGIN.value());
    }

    public Set<String> getAllowedResponseHeaders(String origin) throws IllegalArgumentException {
        return ImmutableSet.of((Object)CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN.value());
    }
}

