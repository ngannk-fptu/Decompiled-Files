/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.EventSource;

public abstract class AbstractEvent
implements Serializable {
    private final EventSource session;

    public AbstractEvent(EventSource source) {
        this.session = source;
    }

    public final EventSource getSession() {
        return this.session;
    }
}

