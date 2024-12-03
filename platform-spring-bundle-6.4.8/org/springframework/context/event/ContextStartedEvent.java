/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

public class ContextStartedEvent
extends ApplicationContextEvent {
    public ContextStartedEvent(ApplicationContext source) {
        super(source);
    }
}

