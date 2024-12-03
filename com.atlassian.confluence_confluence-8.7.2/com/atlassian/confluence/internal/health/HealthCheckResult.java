/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.johnson.event.Event
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.health;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.internal.health.HealthCheck;
import com.atlassian.johnson.event.Event;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public class HealthCheckResult {
    private final Event event;
    private final String cause;
    private final String logMessage;
    private final URL kbUrl;
    private final HealthCheck healthCheck;

    public static List<HealthCheckResult> fail(HealthCheck healthCheck, Event event, @Nullable URL kbUrl, String cause, String logMessage) {
        return Collections.singletonList(new HealthCheckResult(healthCheck, event, kbUrl, cause, logMessage));
    }

    public HealthCheckResult(HealthCheck healthCheck, Event event, @Nullable URL kbUrl, String cause, String logMessage) {
        this.event = Objects.requireNonNull(event);
        this.cause = Objects.requireNonNull(cause);
        this.logMessage = Objects.requireNonNull(logMessage);
        this.kbUrl = kbUrl;
        this.healthCheck = Objects.requireNonNull(healthCheck);
    }

    public @NonNull HealthCheck getHealthCheck() {
        return this.healthCheck;
    }

    public @NonNull Event getEvent() {
        return this.event;
    }

    public @NonNull Optional<URL> getKbUrl() {
        return Optional.ofNullable(this.kbUrl);
    }

    public @NonNull String getCause() {
        return this.cause;
    }

    public @NonNull String getLogMessage() {
        return this.logMessage;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        HealthCheckResult otherResult = (HealthCheckResult)other;
        return new EqualsBuilder().append((Object)this.event, (Object)otherResult.getEvent()).append((Object)this.getHealthCheck(), (Object)otherResult.getHealthCheck()).append((Object)this.cause, (Object)otherResult.getCause()).build();
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.event).append((Object)this.getHealthCheck()).append((Object)this.cause).build();
    }
}

