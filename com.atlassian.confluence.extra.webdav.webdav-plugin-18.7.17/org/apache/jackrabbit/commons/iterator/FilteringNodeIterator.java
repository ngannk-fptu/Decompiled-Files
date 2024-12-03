/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.NoSuchElementException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import org.apache.jackrabbit.commons.predicate.Predicate;

public class FilteringNodeIterator
implements NodeIterator {
    protected final NodeIterator base;
    protected final Predicate filter;
    private Node next;
    private long position;

    public FilteringNodeIterator(NodeIterator base, Predicate filter) {
        this.base = base;
        this.filter = filter;
        this.next = this.seekNext();
    }

    @Override
    public boolean hasNext() {
        return this.next != null;
    }

    public Object next() {
        return this.nextNode();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node nextNode() {
        Node n = this.next;
        if (n == null) {
            throw new NoSuchElementException();
        }
        this.next = this.seekNext();
        ++this.position;
        return n;
    }

    @Override
    public void skip(long skipNum) {
        while (skipNum-- > 0L) {
            this.next();
        }
    }

    @Override
    public long getSize() {
        return -1L;
    }

    @Override
    public long getPosition() {
        return this.position;
    }

    protected Node seekNext() {
        Node n = null;
        while (n == null && this.base.hasNext()) {
            Node nextRes = this.base.nextNode();
            if (!this.filter.evaluate(nextRes)) continue;
            n = nextRes;
        }
        return n;
    }
}

