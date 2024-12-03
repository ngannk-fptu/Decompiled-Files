/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 */
package com.atlassian.troubleshooting.healthcheck.persistence.service;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.troubleshooting.api.healthcheck.HealthCheckStatus;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import java.util.List;

@Transactional
public interface HealthStatusPersistenceService {
    public void storeFailedStatuses(List<HealthCheckStatus> var1);

    public List<HealthCheckStatus> getFailedStatuses(SupportHealthStatus.Severity var1);

    public List<Integer> deleteFailedStatusRecord();
}

