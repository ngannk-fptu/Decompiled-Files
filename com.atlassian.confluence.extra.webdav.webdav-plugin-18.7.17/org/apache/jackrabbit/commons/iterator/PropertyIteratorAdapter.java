/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.Collection;
import java.util.Iterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RangeIterator;
import org.apache.jackrabbit.commons.iterator.RangeIteratorAdapter;
import org.apache.jackrabbit.commons.iterator.RangeIteratorDecorator;

public class PropertyIteratorAdapter
extends RangeIteratorDecorator
implements PropertyIterator {
    public static final PropertyIterator EMPTY = new PropertyIteratorAdapter(RangeIteratorAdapter.EMPTY);

    public PropertyIteratorAdapter(RangeIterator iterator) {
        super(iterator);
    }

    public PropertyIteratorAdapter(Iterator iterator) {
        super(new RangeIteratorAdapter(iterator));
    }

    public PropertyIteratorAdapter(Iterator iterator, long size) {
        super(new RangeIteratorAdapter(iterator, size));
    }

    public PropertyIteratorAdapter(Collection collection) {
        super(new RangeIteratorAdapter(collection));
    }

    @Override
    public Property nextProperty() {
        return (Property)this.next();
    }
}

