/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 */
package com.atlassian.troubleshooting.healthcheck.persistence.service;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.troubleshooting.api.healthcheck.ExtendedSupportHealthCheck;
import java.util.Set;

@Transactional
public interface HealthCheckDisabledService {
    public void setDisabledHealthCheck(ExtendedSupportHealthCheck var1, boolean var2);

    public Set<String> getDisabledHealthChecks();

    public boolean isHealthCheckEnabled(String var1);
}

