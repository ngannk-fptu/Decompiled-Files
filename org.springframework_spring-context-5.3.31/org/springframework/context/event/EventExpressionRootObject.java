/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;

class EventExpressionRootObject {
    private final ApplicationEvent event;
    private final Object[] args;

    public EventExpressionRootObject(ApplicationEvent event, Object[] args) {
        this.event = event;
        this.args = args;
    }

    public ApplicationEvent getEvent() {
        return this.event;
    }

    public Object[] getArgs() {
        return this.args;
    }
}

