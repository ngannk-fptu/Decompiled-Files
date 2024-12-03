/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.config;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

public class ContextLifecycleScheduledTaskRegistrar
extends ScheduledTaskRegistrar
implements SmartInitializingSingleton {
    @Override
    public void afterPropertiesSet() {
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.scheduleTasks();
    }
}

