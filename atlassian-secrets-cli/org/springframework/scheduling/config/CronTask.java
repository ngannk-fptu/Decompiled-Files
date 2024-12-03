/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.config;

import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;

public class CronTask
extends TriggerTask {
    private final String expression;

    public CronTask(Runnable runnable, String expression) {
        this(runnable, new CronTrigger(expression));
    }

    public CronTask(Runnable runnable, CronTrigger cronTrigger) {
        super(runnable, cronTrigger);
        this.expression = cronTrigger.getExpression();
    }

    public String getExpression() {
        return this.expression;
    }
}

