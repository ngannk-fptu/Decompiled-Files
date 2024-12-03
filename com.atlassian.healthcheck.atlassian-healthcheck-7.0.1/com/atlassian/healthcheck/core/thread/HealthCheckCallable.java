/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.healthcheck.core.thread;

import com.atlassian.healthcheck.core.AddressedHealthCheck;
import com.atlassian.healthcheck.core.DefaultHealthStatus;
import com.atlassian.healthcheck.core.HealthStatus;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealthCheckCallable
implements Callable<HealthStatus> {
    private static final Logger log = LoggerFactory.getLogger(HealthCheckCallable.class);
    private final AddressedHealthCheck healthCheck;

    public HealthCheckCallable(AddressedHealthCheck healthCheck) {
        this.healthCheck = healthCheck;
    }

    @Override
    public HealthStatus call() throws Exception {
        log.debug("Invoking health check: {}", (Object)this.healthCheck.getClass().getName());
        try {
            return this.healthCheck.check();
        }
        catch (RuntimeException e) {
            return new DefaultHealthStatus(this.healthCheck, false, "Exception during check invocation: " + e.getMessage());
        }
    }
}

