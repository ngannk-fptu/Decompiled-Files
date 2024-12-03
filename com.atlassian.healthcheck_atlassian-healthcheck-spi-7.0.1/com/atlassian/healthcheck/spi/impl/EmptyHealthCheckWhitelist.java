/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.healthcheck.spi.impl;

import com.atlassian.healthcheck.spi.HealthCheckWhitelist;
import java.util.Collections;
import java.util.Set;

public class EmptyHealthCheckWhitelist
implements HealthCheckWhitelist {
    @Override
    public Set<String> getWhitelistedItemsForHealthCheck(String whitelistKey) {
        return Collections.emptySet();
    }
}

