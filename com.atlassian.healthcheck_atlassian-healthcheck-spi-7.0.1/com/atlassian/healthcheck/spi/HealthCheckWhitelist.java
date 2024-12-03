/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.healthcheck.spi;

import java.util.Set;

public interface HealthCheckWhitelist {
    public Set<String> getWhitelistedItemsForHealthCheck(String var1);
}

