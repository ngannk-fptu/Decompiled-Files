/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import org.springframework.context.ApplicationEvent;

@FunctionalInterface
public interface ApplicationEventPublisher {
    default public void publishEvent(ApplicationEvent event) {
        this.publishEvent((Object)event);
    }

    public void publishEvent(Object var1);
}

