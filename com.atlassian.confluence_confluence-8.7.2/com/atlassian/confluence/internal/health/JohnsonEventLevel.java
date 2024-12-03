/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.johnson.event.EventLevel
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.internal.health;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.johnson.event.EventLevel;
import java.util.Arrays;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
public enum JohnsonEventLevel {
    FATAL("fatal"),
    ERROR("error"),
    WARNING("warning");

    private final String johnsonLevel;

    public static JohnsonEventLevel withName(String levelName) {
        return Arrays.stream(JohnsonEventLevel.values()).filter(value -> value.levelName().equalsIgnoreCase(levelName)).findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("No JohnsonEventLevel called '%s'", levelName)));
    }

    private JohnsonEventLevel(String johnsonLevelName) {
        this.johnsonLevel = Objects.requireNonNull(johnsonLevelName);
    }

    public boolean isAtLeast(JohnsonEventLevel minimumLevel) {
        return minimumLevel.ordinal() >= this.ordinal();
    }

    public @NonNull String levelName() {
        return this.johnsonLevel;
    }

    public @NonNull EventLevel level() {
        return Objects.requireNonNull(EventLevel.get((String)this.johnsonLevel));
    }
}

