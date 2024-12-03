/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.event;

import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.EventType;

public interface EventSource {
    public <T extends Event> void addEventListener(EventType<T> var1, EventListener<? super T> var2);

    public <T extends Event> boolean removeEventListener(EventType<T> var1, EventListener<? super T> var2);
}

