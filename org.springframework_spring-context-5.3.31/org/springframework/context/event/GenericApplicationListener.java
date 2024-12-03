/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ResolvableType
 */
package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.ResolvableType;

public interface GenericApplicationListener
extends SmartApplicationListener {
    @Override
    default public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return this.supportsEventType(ResolvableType.forClass(eventType));
    }

    public boolean supportsEventType(ResolvableType var1);
}

