/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 */
package com.atlassian.spring.container;

import org.springframework.context.ApplicationEvent;

public class ContainerContextLoadedEvent
extends ApplicationEvent {
    public ContainerContextLoadedEvent(Object o) {
        super(o);
    }
}

