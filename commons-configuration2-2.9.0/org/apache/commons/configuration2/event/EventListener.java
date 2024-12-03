/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.event;

import org.apache.commons.configuration2.event.Event;

public interface EventListener<T extends Event> {
    public void onEvent(T var1);
}

