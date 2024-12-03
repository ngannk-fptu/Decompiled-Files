/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ComponentNotFoundException
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.health.analytics;

import com.atlassian.confluence.internal.health.analytics.HealthCheckAnalyticsSender;
import com.atlassian.spring.container.ComponentNotFoundException;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealthCheckAnalyticsSenderSupplier
implements Supplier<HealthCheckAnalyticsSender> {
    private static final Logger log = LoggerFactory.getLogger(HealthCheckAnalyticsSenderSupplier.class);
    private final HealthCheckAnalyticsSender bootstrapHealthCheckAnalyticsSender;

    public HealthCheckAnalyticsSenderSupplier(HealthCheckAnalyticsSender bootstrapHealthCheckAnalyticsSender) {
        this.bootstrapHealthCheckAnalyticsSender = Objects.requireNonNull(bootstrapHealthCheckAnalyticsSender);
    }

    @VisibleForTesting
    HealthCheckAnalyticsSender getProductionHealthCheckAnalyticsSender() {
        return (HealthCheckAnalyticsSender)ContainerManager.getComponent((String)"productionHealthCheckAnalyticsSender", HealthCheckAnalyticsSender.class);
    }

    public HealthCheckAnalyticsSender get() {
        if (ContainerManager.isContainerSetup()) {
            try {
                return this.getProductionHealthCheckAnalyticsSender();
            }
            catch (ComponentNotFoundException e) {
                log.error("productionHealthCheckAnalyticsSender not found", (Throwable)e);
            }
        }
        return this.bootstrapHealthCheckAnalyticsSender;
    }
}

