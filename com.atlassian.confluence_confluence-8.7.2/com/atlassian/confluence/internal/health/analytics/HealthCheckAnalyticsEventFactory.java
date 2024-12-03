/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.johnson.event.Event
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.internal.health.analytics;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.internal.health.analytics.HealthCheckAnalyticsEvent;
import com.atlassian.johnson.event.Event;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
public interface HealthCheckAnalyticsEventFactory {
    public @NonNull HealthCheckAnalyticsEvent forHealthCheckResult(Event var1);

    public @NonNull HealthCheckAnalyticsEvent forJohnsonHelpLinkClicked(Event var1);
}

