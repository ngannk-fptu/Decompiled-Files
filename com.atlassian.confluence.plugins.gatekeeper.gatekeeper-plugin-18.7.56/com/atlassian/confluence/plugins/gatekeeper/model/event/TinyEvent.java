/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.event;

import com.atlassian.confluence.plugins.gatekeeper.model.event.EventCategory;
import com.atlassian.confluence.plugins.gatekeeper.model.event.EventType;
import java.io.Serializable;

public class TinyEvent
implements Serializable {
    private static final long serialVersionUID = -1352820532661856389L;
    public static final TinyEvent POISON_PILL = new PoisonPill();
    protected EventType eventType;

    protected TinyEvent(EventType eventType) {
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public EventCategory getEventCategory() {
        return this.eventType.getCategory();
    }

    private static final class PoisonPill
    extends TinyEvent {
        private static final long serialVersionUID = 5200856647006212526L;

        PoisonPill() {
            super(null);
        }

        @Override
        public EventCategory getEventCategory() {
            throw new IllegalStateException("This object is not meant to be used as an actual TinyEvent");
        }

        @Override
        public EventType getEventType() {
            throw new IllegalStateException("This object is not meant to be used as an actual TinyEvent");
        }

        public boolean equals(Object obj) {
            return this == obj;
        }

        public int hashCode() {
            return 0;
        }
    }
}

