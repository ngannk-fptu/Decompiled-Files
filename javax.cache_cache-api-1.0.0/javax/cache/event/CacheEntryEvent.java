/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.event;

import java.util.EventObject;
import javax.cache.Cache;
import javax.cache.event.EventType;

public abstract class CacheEntryEvent<K, V>
extends EventObject
implements Cache.Entry<K, V> {
    private EventType eventType;

    public CacheEntryEvent(Cache source, EventType eventType) {
        super(source);
        this.eventType = eventType;
    }

    @Override
    public final Cache getSource() {
        return (Cache)super.getSource();
    }

    public abstract V getOldValue();

    public abstract boolean isOldValueAvailable();

    public final EventType getEventType() {
        return this.eventType;
    }
}

