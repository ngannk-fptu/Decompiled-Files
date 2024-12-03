/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.event;

import com.atlassian.event.Event;

@Deprecated
public interface EventListener {
    public void handleEvent(Event var1);

    public Class[] getHandledEventClasses();
}

