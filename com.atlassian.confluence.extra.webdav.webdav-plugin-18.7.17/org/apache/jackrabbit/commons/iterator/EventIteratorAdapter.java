/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.jcr.RangeIterator;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import org.apache.jackrabbit.commons.iterator.RangeIteratorAdapter;
import org.apache.jackrabbit.commons.iterator.RangeIteratorDecorator;

public class EventIteratorAdapter
extends RangeIteratorDecorator
implements EventIterator {
    public EventIteratorAdapter(EventIterator iterator) {
        super(iterator);
    }

    public EventIteratorAdapter(RangeIterator iterator) {
        super(iterator);
    }

    public EventIteratorAdapter(Iterator iterator) {
        super(new RangeIteratorAdapter(iterator));
    }

    public EventIteratorAdapter(Collection collection) {
        super(new RangeIteratorAdapter(collection));
    }

    @Override
    public Event nextEvent() throws NoSuchElementException {
        return (Event)this.next();
    }
}

