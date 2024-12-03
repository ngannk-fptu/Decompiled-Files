/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import java.util.Iterator;
import org.apache.jackrabbit.spi.Event;

public interface EventBundle
extends Iterable<Event> {
    public Iterator<Event> getEvents();

    public boolean isLocal();
}

