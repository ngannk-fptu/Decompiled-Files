/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.observation;

import javax.jcr.RangeIterator;
import javax.jcr.observation.EventListener;

public interface EventListenerIterator
extends RangeIterator {
    public EventListener nextEventListener();
}

