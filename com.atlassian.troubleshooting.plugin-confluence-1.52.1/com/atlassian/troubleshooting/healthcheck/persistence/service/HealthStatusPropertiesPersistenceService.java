/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 */
package com.atlassian.troubleshooting.healthcheck.persistence.service;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.troubleshooting.healthcheck.rest.HealthCheckPropertiesRepresentation;

@Transactional
public interface HealthStatusPropertiesPersistenceService {
    public void storeLastRun();

    public HealthCheckPropertiesRepresentation getLastRun();
}

