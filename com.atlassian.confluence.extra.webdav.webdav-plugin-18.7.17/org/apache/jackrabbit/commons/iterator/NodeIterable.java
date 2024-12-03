/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.Iterator;
import javax.jcr.Node;
import javax.jcr.NodeIterator;

@Deprecated
public class NodeIterable
implements Iterable<Node> {
    private final NodeIterator iterator;

    public NodeIterable(NodeIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    public Iterator<Node> iterator() {
        return this.iterator;
    }
}

