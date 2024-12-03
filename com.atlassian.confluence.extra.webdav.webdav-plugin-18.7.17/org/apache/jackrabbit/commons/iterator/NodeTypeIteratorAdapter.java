/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.jcr.RangeIterator;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import org.apache.jackrabbit.commons.iterator.RangeIteratorAdapter;
import org.apache.jackrabbit.commons.iterator.RangeIteratorDecorator;

public class NodeTypeIteratorAdapter
extends RangeIteratorDecorator
implements NodeTypeIterator {
    public static final NodeTypeIterator EMPTY = new NodeTypeIteratorAdapter(RangeIteratorAdapter.EMPTY);

    public NodeTypeIteratorAdapter(RangeIterator iterator) {
        super(iterator);
    }

    public NodeTypeIteratorAdapter(Iterator iterator) {
        super(new RangeIteratorAdapter(iterator));
    }

    public NodeTypeIteratorAdapter(Collection<NodeType> collection) {
        super(new RangeIteratorAdapter(collection));
    }

    @Override
    public NodeType nextNodeType() throws NoSuchElementException {
        return (NodeType)this.next();
    }
}

