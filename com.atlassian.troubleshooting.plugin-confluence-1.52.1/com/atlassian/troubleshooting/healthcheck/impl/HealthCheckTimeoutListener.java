/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.impl;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.troubleshooting.api.healthcheck.ExtendedSupportHealthCheck;
import com.atlassian.troubleshooting.healthcheck.event.SupportHealthCheckTimeoutEvent;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;

public class HealthCheckTimeoutListener
implements Consumer<ExtendedSupportHealthCheck> {
    private final ApplicationProperties applicationProperties;
    private final EventPublisher eventPublisher;

    @Autowired
    public HealthCheckTimeoutListener(@Nonnull ApplicationProperties applicationProperties, @Nonnull EventPublisher eventPublisher) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public void accept(ExtendedSupportHealthCheck healthCheck) {
        SupportHealthCheckTimeoutEvent event = new SupportHealthCheckTimeoutEvent(this.applicationProperties.getDisplayName(), healthCheck.getName(), healthCheck.getTimeOut(), this.applicationProperties.getVersion());
        this.eventPublisher.publish((Object)event);
    }
}

