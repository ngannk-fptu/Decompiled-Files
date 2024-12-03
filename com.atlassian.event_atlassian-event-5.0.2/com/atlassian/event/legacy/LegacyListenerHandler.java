/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Sets
 */
package com.atlassian.event.legacy;

import com.atlassian.event.Event;
import com.atlassian.event.EventListener;
import com.atlassian.event.spi.ListenerHandler;
import com.atlassian.event.spi.ListenerInvoker;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LegacyListenerHandler
implements ListenerHandler {
    @Override
    public List<ListenerInvoker> getInvokers(Object listener) {
        return Preconditions.checkNotNull((Object)listener) instanceof EventListener ? this.getLegacyListenerInvoker((EventListener)listener) : Collections.emptyList();
    }

    private List<ListenerInvoker> getLegacyListenerInvoker(EventListener eventListener) {
        return Collections.singletonList(new LegacyListenerInvoker(eventListener));
    }

    private static class LegacyListenerInvoker
    implements ListenerInvoker {
        private final EventListener eventListener;

        LegacyListenerInvoker(EventListener eventListener) {
            this.eventListener = (EventListener)Preconditions.checkNotNull((Object)eventListener);
        }

        @Override
        public Set<Class<?>> getSupportedEventTypes() {
            Object[] classes = this.eventListener.getHandledEventClasses();
            if (classes.length == 0) {
                return Collections.singleton(Event.class);
            }
            return Sets.newHashSet((Object[])classes);
        }

        @Override
        public void invoke(Object event) {
            this.eventListener.handleEvent((Event)((Object)event));
        }

        @Override
        public boolean supportAsynchronousEvents() {
            return true;
        }

        public String toString() {
            return "LegacyListenerInvoker{eventListener=" + this.eventListener + '}';
        }
    }
}

