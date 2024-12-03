/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.johnson.event;

import com.atlassian.johnson.Johnson;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class EventLevel {
    public static final String ERROR = "error";
    public static final String FATAL = "fatal";
    public static final String WARNING = "warning";
    private final String description;
    private final String level;

    public EventLevel(String level, String description) {
        this.description = Objects.requireNonNull(description, "description");
        this.level = Objects.requireNonNull(level, "level");
    }

    @Nullable
    public static EventLevel get(String level) {
        return Johnson.getConfig().getEventLevel(level);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof EventLevel) {
            EventLevel e = (EventLevel)o;
            return Objects.equals(this.getDescription(), e.getDescription()) && Objects.equals(this.getLevel(), e.getLevel());
        }
        return false;
    }

    @Nonnull
    public String getDescription() {
        return this.description;
    }

    @Nonnull
    public String getLevel() {
        return this.level;
    }

    public int hashCode() {
        return Objects.hash(this.getLevel(), this.getDescription());
    }

    public String toString() {
        return "(EventLevel: " + this.level + ")";
    }
}

