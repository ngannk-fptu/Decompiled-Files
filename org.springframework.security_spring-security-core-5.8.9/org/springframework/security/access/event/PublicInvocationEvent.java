/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access.event;

import org.springframework.security.access.event.AbstractAuthorizationEvent;

@Deprecated
public class PublicInvocationEvent
extends AbstractAuthorizationEvent {
    public PublicInvocationEvent(Object secureObject) {
        super(secureObject);
    }
}

