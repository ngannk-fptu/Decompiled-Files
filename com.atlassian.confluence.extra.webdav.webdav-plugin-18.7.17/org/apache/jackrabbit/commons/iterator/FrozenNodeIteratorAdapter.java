/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;
import org.apache.jackrabbit.commons.iterator.RangeIteratorAdapter;

public class FrozenNodeIteratorAdapter
extends RangeIteratorAdapter
implements NodeIterator {
    public FrozenNodeIteratorAdapter(VersionIterator iterator) {
        super(iterator);
    }

    @Override
    public Node nextNode() {
        try {
            return ((Version)this.next()).getFrozenNode();
        }
        catch (RepositoryException e) {
            throw (IllegalStateException)new IllegalStateException(e.toString()).initCause(e);
        }
    }
}

