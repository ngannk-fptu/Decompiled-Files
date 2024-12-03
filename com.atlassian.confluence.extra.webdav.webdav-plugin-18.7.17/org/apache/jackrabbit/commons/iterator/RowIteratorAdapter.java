/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.Collection;
import java.util.Iterator;
import javax.jcr.RangeIterator;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import org.apache.jackrabbit.commons.iterator.RangeIteratorAdapter;
import org.apache.jackrabbit.commons.iterator.RangeIteratorDecorator;

public class RowIteratorAdapter
extends RangeIteratorDecorator
implements RowIterator {
    public static final RowIterator EMPTY = new RowIteratorAdapter(RangeIteratorAdapter.EMPTY);

    public RowIteratorAdapter(RangeIterator iterator) {
        super(iterator);
    }

    public RowIteratorAdapter(Iterator iterator) {
        super(new RangeIteratorAdapter(iterator));
    }

    public RowIteratorAdapter(Collection collection) {
        super(new RangeIteratorAdapter(collection));
    }

    @Override
    public Row nextRow() {
        return (Row)this.next();
    }
}

