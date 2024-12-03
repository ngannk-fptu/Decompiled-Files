/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.annotation;

import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@FunctionalInterface
public interface SchedulingConfigurer {
    public void configureTasks(ScheduledTaskRegistrar var1);
}

