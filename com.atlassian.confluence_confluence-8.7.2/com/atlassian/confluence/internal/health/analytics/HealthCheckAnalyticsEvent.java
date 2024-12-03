/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.health.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
@EventName(value="health.check.completed")
public class HealthCheckAnalyticsEvent {
    private static final String PRODUCT_RUN_ID = UUID.randomUUID().toString();
    private final String cause;
    private final String checkId;
    private final String eventId;
    private final String eventLevel;
    private final String startupMode;
    private final URL kbURL;

    public static @NonNull String sanitise(String text) {
        return UUID.nameUUIDFromBytes(text.getBytes(StandardCharsets.UTF_8)).toString();
    }

    public HealthCheckAnalyticsEvent(String checkId, String startupMode, String eventId, String eventLevel, String cause, @Nullable URL kbURL) {
        this.checkId = Objects.requireNonNull(checkId);
        this.startupMode = Objects.requireNonNull(startupMode);
        this.eventId = Objects.requireNonNull(eventId);
        this.eventLevel = Objects.requireNonNull(eventLevel);
        this.cause = Objects.requireNonNull(cause);
        this.kbURL = kbURL;
    }

    public String getProductRunId() {
        return PRODUCT_RUN_ID;
    }

    public String getCheckId() {
        return HealthCheckAnalyticsEvent.sanitise(this.checkId);
    }

    public String getCause() {
        return HealthCheckAnalyticsEvent.sanitise(this.cause);
    }

    public String getEventId() {
        return this.eventId;
    }

    public String getEventLevel() {
        return this.eventLevel;
    }

    public String getStartupMode() {
        return this.startupMode;
    }

    public @Nullable String getKbURL() {
        return Optional.ofNullable(this.kbURL).map(url -> HealthCheckAnalyticsEvent.sanitise(url.toString())).orElse(null);
    }
}

