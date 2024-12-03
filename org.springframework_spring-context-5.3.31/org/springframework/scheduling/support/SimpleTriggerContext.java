/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.scheduling.support;

import java.time.Clock;
import java.util.Date;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TriggerContext;

public class SimpleTriggerContext
implements TriggerContext {
    private final Clock clock;
    @Nullable
    private volatile Date lastScheduledExecutionTime;
    @Nullable
    private volatile Date lastActualExecutionTime;
    @Nullable
    private volatile Date lastCompletionTime;

    public SimpleTriggerContext() {
        this.clock = Clock.systemDefaultZone();
    }

    public SimpleTriggerContext(Date lastScheduledExecutionTime, Date lastActualExecutionTime, Date lastCompletionTime) {
        this();
        this.lastScheduledExecutionTime = lastScheduledExecutionTime;
        this.lastActualExecutionTime = lastActualExecutionTime;
        this.lastCompletionTime = lastCompletionTime;
    }

    public SimpleTriggerContext(Clock clock) {
        this.clock = clock;
    }

    public void update(Date lastScheduledExecutionTime, Date lastActualExecutionTime, Date lastCompletionTime) {
        this.lastScheduledExecutionTime = lastScheduledExecutionTime;
        this.lastActualExecutionTime = lastActualExecutionTime;
        this.lastCompletionTime = lastCompletionTime;
    }

    @Override
    public Clock getClock() {
        return this.clock;
    }

    @Override
    @Nullable
    public Date lastScheduledExecutionTime() {
        return this.lastScheduledExecutionTime;
    }

    @Override
    @Nullable
    public Date lastActualExecutionTime() {
        return this.lastActualExecutionTime;
    }

    @Override
    @Nullable
    public Date lastCompletionTime() {
        return this.lastCompletionTime;
    }
}

