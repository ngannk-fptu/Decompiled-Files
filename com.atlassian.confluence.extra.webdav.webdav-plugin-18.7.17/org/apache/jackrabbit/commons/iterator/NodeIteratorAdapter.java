/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RangeIterator;
import org.apache.jackrabbit.commons.iterator.RangeIteratorAdapter;
import org.apache.jackrabbit.commons.iterator.RangeIteratorDecorator;

public class NodeIteratorAdapter
extends RangeIteratorDecorator
implements NodeIterator {
    public static final NodeIterator EMPTY = new NodeIteratorAdapter(RangeIteratorAdapter.EMPTY);

    public NodeIteratorAdapter(RangeIterator iterator) {
        super(iterator);
    }

    public NodeIteratorAdapter(Iterator iterator) {
        super(new RangeIteratorAdapter(iterator));
    }

    public NodeIteratorAdapter(Iterator iterator, long size) {
        super(new RangeIteratorAdapter(iterator, size));
    }

    public NodeIteratorAdapter(Collection collection) {
        super(new RangeIteratorAdapter(collection));
    }

    @Override
    public Node nextNode() throws NoSuchElementException {
        return (Node)this.next();
    }
}

