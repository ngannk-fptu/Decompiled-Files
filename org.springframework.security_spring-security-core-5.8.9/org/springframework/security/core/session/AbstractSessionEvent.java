/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 */
package org.springframework.security.core.session;

import org.springframework.context.ApplicationEvent;

public class AbstractSessionEvent
extends ApplicationEvent {
    public AbstractSessionEvent(Object source) {
        super(source);
    }
}

