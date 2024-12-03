/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.scheduling;

import java.time.Clock;
import java.util.Date;
import org.springframework.lang.Nullable;

public interface TriggerContext {
    default public Clock getClock() {
        return Clock.systemDefaultZone();
    }

    @Nullable
    public Date lastScheduledExecutionTime();

    @Nullable
    public Date lastActualExecutionTime();

    @Nullable
    public Date lastCompletionTime();
}

