/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  net.java.ao.ActiveObjectsException
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.persistence.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.troubleshooting.api.healthcheck.ExtendedSupportHealthCheck;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthCheckDisabledService;
import com.atlassian.troubleshooting.stp.persistence.SupportHealthcheckSchema;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import net.java.ao.ActiveObjectsException;
import net.java.ao.DBParam;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class HealthCheckDisabledServiceImpl
implements HealthCheckDisabledService {
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckDisabledServiceImpl.class);
    private final ActiveObjects ao;

    @Autowired
    public HealthCheckDisabledServiceImpl(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    public void setDisabledHealthCheck(ExtendedSupportHealthCheck healthCheck, boolean isEnabled) {
        if (isEnabled) {
            this.removeDisabledHealthCheck(healthCheck);
        } else {
            this.storeDisabledHealthCheck(healthCheck);
        }
    }

    @Override
    public Set<String> getDisabledHealthChecks() {
        return Arrays.stream(this.ao.find(SupportHealthcheckSchema.DisabledHealthChecks.class)).map(status -> status.getHealthCheckKey()).collect(Collectors.toSet());
    }

    private void storeDisabledHealthCheck(ExtendedSupportHealthCheck healthCheck) {
        try {
            if (this.isHealthCheckEnabled(healthCheck.getKey())) {
                ((SupportHealthcheckSchema.DisabledHealthChecks)this.ao.create(SupportHealthcheckSchema.DisabledHealthChecks.class, new DBParam[]{new DBParam("HEALTH_CHECK_KEY", (Object)healthCheck.getKey())})).save();
            }
            LOG.debug("{} check has been disabled and persisted to the AO table.", (Object)healthCheck.getName());
        }
        catch (ActiveObjectsException e) {
            LOG.error("There's a problem persisting new disabled check, {}, into the database", (Object)healthCheck.getName(), (Object)e);
        }
    }

    private void removeDisabledHealthCheck(ExtendedSupportHealthCheck healthCheck) {
        SupportHealthcheckSchema.DisabledHealthChecks[] statuses;
        for (SupportHealthcheckSchema.DisabledHealthChecks checkStatus : statuses = (SupportHealthcheckSchema.DisabledHealthChecks[])this.ao.find(SupportHealthcheckSchema.DisabledHealthChecks.class, Query.select().where("HEALTH_CHECK_KEY = ?", new Object[]{healthCheck.getKey()}))) {
            LOG.debug("{} check has been removed from the Disabled Health Checks AO table.", (Object)healthCheck.getName());
            this.ao.delete(new RawEntity[]{checkStatus});
        }
    }

    @Override
    public boolean isHealthCheckEnabled(String healthCheckKey) {
        return this.ao.count(SupportHealthcheckSchema.DisabledHealthChecks.class, Query.select().where("HEALTH_CHECK_KEY = ?", new Object[]{healthCheckKey})) == 0;
    }
}

