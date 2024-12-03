/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.health.checks;

import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class HomeHealthCheckFailure {
    private final String configuredHome;
    private final Reason reason;

    public static @NonNull HomeHealthCheckFailure missingConfiguration(@NonNull Reason reason) {
        return new HomeHealthCheckFailure(reason, null);
    }

    public static @NonNull HomeHealthCheckFailure badConfiguredHome(@NonNull Reason reason, @NonNull String configuredHome) {
        return new HomeHealthCheckFailure(reason, configuredHome);
    }

    private HomeHealthCheckFailure(@NonNull Reason reason, @Nullable String configuredHome) {
        this.reason = Objects.requireNonNull(reason);
        this.configuredHome = configuredHome;
    }

    public @NonNull Reason getReason() {
        return this.reason;
    }

    public @NonNull String getConfiguredHome() {
        return Objects.requireNonNull(this.configuredHome);
    }

    public static enum Reason {
        NOT_CONFIGURED("not-configured"),
        PATH_NOT_ABSOLUTE("path-not-absolute"),
        NOT_A_DIR("not-a-dir"),
        CREATION_FAILED_WRITE_PERMISSION("creation-failed-write-permission");

        private final String analyticsValue;

        private Reason(String analyticsValue) {
            this.analyticsValue = Objects.requireNonNull(analyticsValue);
        }

        public @NonNull String getAnalyticsValue() {
            return this.analyticsValue;
        }
    }
}

