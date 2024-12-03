/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.config;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.config.Task;
import org.springframework.util.Assert;

public class TriggerTask
extends Task {
    private final Trigger trigger;

    public TriggerTask(Runnable runnable, Trigger trigger) {
        super(runnable);
        Assert.notNull((Object)trigger, "Trigger must not be null");
        this.trigger = trigger;
    }

    public Trigger getTrigger() {
        return this.trigger;
    }
}

