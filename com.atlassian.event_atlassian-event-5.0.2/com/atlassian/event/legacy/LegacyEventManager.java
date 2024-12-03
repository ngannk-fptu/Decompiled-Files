/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.event.legacy;

import com.atlassian.event.Event;
import com.atlassian.event.EventListener;
import com.atlassian.event.EventManager;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public final class LegacyEventManager
implements EventManager {
    private final EventPublisher delegateEventPublisher;
    private final Map<String, EventListener> legacyListeners = Maps.newHashMap();

    public LegacyEventManager(EventPublisher delegateEventPublisher) {
        this.delegateEventPublisher = (EventPublisher)Preconditions.checkNotNull((Object)delegateEventPublisher);
    }

    @Override
    public void publishEvent(Event event) {
        this.delegateEventPublisher.publish(Preconditions.checkNotNull((Object)((Object)event)));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void registerListener(String listenerKey, EventListener listener) {
        LegacyEventManager.checkListenerKey(listenerKey);
        Preconditions.checkNotNull((Object)listener);
        Map<String, EventListener> map = this.legacyListeners;
        synchronized (map) {
            EventListener registeredListener = this.legacyListeners.get(listenerKey);
            if (registeredListener != null) {
                this.delegateEventPublisher.unregister(registeredListener);
            }
            this.legacyListeners.put(listenerKey, listener);
            this.delegateEventPublisher.register(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unregisterListener(String listenerKey) {
        LegacyEventManager.checkListenerKey(listenerKey);
        Map<String, EventListener> map = this.legacyListeners;
        synchronized (map) {
            EventListener listener = this.legacyListeners.get(listenerKey);
            if (listener != null) {
                this.delegateEventPublisher.unregister(listener);
            }
        }
    }

    private static void checkListenerKey(String listenerKey) {
        if (StringUtils.isEmpty((CharSequence)listenerKey)) {
            throw new IllegalArgumentException("listenerKey must not be empty");
        }
    }
}

