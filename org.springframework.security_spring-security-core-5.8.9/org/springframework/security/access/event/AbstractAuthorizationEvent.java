/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 */
package org.springframework.security.access.event;

import org.springframework.context.ApplicationEvent;

@Deprecated
public abstract class AbstractAuthorizationEvent
extends ApplicationEvent {
    public AbstractAuthorizationEvent(Object secureObject) {
        super(secureObject);
    }
}

