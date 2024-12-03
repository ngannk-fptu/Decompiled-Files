/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.EventSource;

public class ClearEvent
extends AbstractEvent {
    public ClearEvent(EventSource source) {
        super(source);
    }
}

