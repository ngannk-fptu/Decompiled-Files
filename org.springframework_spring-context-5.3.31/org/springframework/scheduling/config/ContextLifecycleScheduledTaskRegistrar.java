/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.SmartInitializingSingleton
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

    public void afterSingletonsInstantiated() {
        this.scheduleTasks();
    }
}

