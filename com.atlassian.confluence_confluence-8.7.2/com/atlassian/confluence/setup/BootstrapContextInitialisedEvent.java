/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 */
package com.atlassian.confluence.setup;

import org.springframework.context.ApplicationEvent;

public class BootstrapContextInitialisedEvent
extends ApplicationEvent {
    private static final long serialVersionUID = 3615996049086676874L;

    public BootstrapContextInitialisedEvent(Object o) {
        super(o);
    }
}

