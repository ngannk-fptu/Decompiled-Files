/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.observation;

import javax.jcr.RangeIterator;
import javax.jcr.observation.Event;

public interface EventIterator
extends RangeIterator {
    public Event nextEvent();
}

