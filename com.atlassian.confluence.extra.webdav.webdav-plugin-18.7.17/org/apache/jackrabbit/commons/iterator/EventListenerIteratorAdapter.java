/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.Collection;
import java.util.Iterator;
import javax.jcr.RangeIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.EventListenerIterator;
import org.apache.jackrabbit.commons.iterator.RangeIteratorAdapter;
import org.apache.jackrabbit.commons.iterator.RangeIteratorDecorator;

public class EventListenerIteratorAdapter
extends RangeIteratorDecorator
implements EventListenerIterator {
    public static final EventListenerIterator EMPTY = new EventListenerIteratorAdapter(RangeIteratorAdapter.EMPTY);

    public EventListenerIteratorAdapter(RangeIterator iterator) {
        super(iterator);
    }

    public EventListenerIteratorAdapter(Iterator iterator) {
        super(new RangeIteratorAdapter(iterator));
    }

    public EventListenerIteratorAdapter(Collection collection) {
        super(new RangeIteratorAdapter(collection));
    }

    @Override
    public EventListener nextEventListener() {
        return (EventListener)this.next();
    }
}

