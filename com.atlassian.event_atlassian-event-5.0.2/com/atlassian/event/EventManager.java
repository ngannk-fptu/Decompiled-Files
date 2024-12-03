/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.event;

import com.atlassian.event.Event;
import com.atlassian.event.EventListener;

@Deprecated
public interface EventManager {
    public void publishEvent(Event var1);

    public void registerListener(String var1, EventListener var2);

    public void unregisterListener(String var1);
}

