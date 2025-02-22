/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.graph.ElementTypesAreNonnullByDefault;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
abstract class MultiEdgesConnecting<E>
extends AbstractSet<E> {
    private final Map<E, ?> outEdgeToNode;
    private final Object targetNode;

    MultiEdgesConnecting(Map<E, ?> outEdgeToNode, Object targetNode) {
        this.outEdgeToNode = Preconditions.checkNotNull(outEdgeToNode);
        this.targetNode = Preconditions.checkNotNull(targetNode);
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        final Iterator<Map.Entry<E, ?>> entries = this.outEdgeToNode.entrySet().iterator();
        return new AbstractIterator<E>(){

            @Override
            @CheckForNull
            protected E computeNext() {
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry)entries.next();
                    if (!MultiEdgesConnecting.this.targetNode.equals(entry.getValue())) continue;
                    return entry.getKey();
                }
                return this.endOfData();
            }
        };
    }

    @Override
    public boolean contains(@CheckForNull Object edge) {
        return this.targetNode.equals(this.outEdgeToNode.get(edge));
    }
}

