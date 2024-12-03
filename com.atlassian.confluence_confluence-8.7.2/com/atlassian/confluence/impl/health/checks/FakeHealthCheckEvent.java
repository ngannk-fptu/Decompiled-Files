/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.event.EventLevel
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.health.checks;

import com.atlassian.johnson.event.EventLevel;
import org.checkerframework.checker.nullness.qual.NonNull;

class FakeHealthCheckEvent {
    private final EventLevel eventLevel;
    private final boolean isDismissible;

    static @NonNull FakeHealthCheckEvent createPhase(String error) {
        String[] errorParsed = error.split("-");
        boolean isDismissible = errorParsed.length > 1 && errorParsed[1].equals("dismissible");
        return new FakeHealthCheckEvent(errorParsed[0], isDismissible);
    }

    private FakeHealthCheckEvent(String level, boolean isDismissible) {
        this.eventLevel = EventLevel.get((String)level);
        this.isDismissible = isDismissible;
    }

    EventLevel getLevel() {
        return this.eventLevel;
    }

    boolean isDismissible() {
        return this.isDismissible;
    }
}

