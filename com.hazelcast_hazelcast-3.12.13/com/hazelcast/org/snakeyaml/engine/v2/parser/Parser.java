/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.parser;

import com.hazelcast.org.snakeyaml.engine.v2.events.Event;
import java.util.Iterator;

public interface Parser
extends Iterator<Event> {
    public boolean checkEvent(Event.ID var1);

    public Event peekEvent();

    @Override
    public Event next();
}

