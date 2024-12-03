/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.security.descriptor;

import java.util.Set;

public interface CorsDefaults {
    public boolean allowsCredentials(String var1);

    public boolean allowsOrigin(String var1);

    public Set<String> getAllowedRequestHeaders(String var1);

    public Set<String> getAllowedResponseHeaders(String var1) throws IllegalArgumentException;
}

